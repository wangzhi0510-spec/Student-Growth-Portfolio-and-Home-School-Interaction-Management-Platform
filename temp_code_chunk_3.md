
### backend/src/main/java/com/student/growth/service/AuthService.java
```java
package com.student.growth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.dto.LoginRequest;
import com.student.growth.dto.LoginResponse;
import com.student.growth.dto.RegisterRequest;
import com.student.growth.entity.StudentInfo;
import com.student.growth.entity.User;
import com.student.growth.mapper.StudentInfoMapper;
import com.student.growth.mapper.UserMapper;
import com.student.growth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        return response;
    }

    @Transactional
    public void register(RegisterRequest request) {
        User existUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 验证家长注册时必须提供有效的学号
        if ("PARENT".equals(request.getRole())) {
            if (request.getStudentNo() == null || request.getStudentNo().isEmpty()) {
                throw new RuntimeException("家长注册必须填写学生学号");
            }
            StudentInfo student = studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>()
                    .eq(StudentInfo::getStudentNo, request.getStudentNo()));
            if (student == null) {
                throw new RuntimeException("学号不存在");
            }
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setStatus(1);
        userMapper.insert(user);

        // 如果是家长注册，需要关联学生
        if ("PARENT".equals(request.getRole())) {
            StudentInfo student = studentInfoMapper.selectOne(new LambdaQueryWrapper<StudentInfo>()
                    .eq(StudentInfo::getStudentNo, request.getStudentNo()));
            student.setParentId(user.getId());
            studentInfoMapper.updateById(student);
        }
    }

    public User getCurrentUser(Long userId) {
        return userMapper.selectById(userId);
    }
}
```

### backend/src/main/java/com/student/growth/service/NoticeService.java
```java
package com.student.growth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.Notice;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    public Page<Notice> getNoticePage(Integer current, Integer size) {
        Page<Notice> page = new Page<>(current, size);
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        // 获取当前用户绑定的学生列表（如果是家长）
        List<StudentInfo> students = studentInfoService.getByParentId(userId);
        List<Long> classIds = students.stream()
                .map(StudentInfo::getClassId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        wrapper.eq(Notice::getType, "school")
               .or(w -> w.eq(Notice::getType, "class").in(classIds.size() > 0, Notice::getTargetId, classIds))
               .orderByDesc(Notice::getPublishTime);
               
        Page<Notice> result = page(page, wrapper);
        
        // 填充目标名称
        result.getRecords().forEach(notice -> {
            if ("school".equals(notice.getType())) {
                notice.setTargetName("学校通知");
            } else if ("class".equals(notice.getType()) && notice.getTargetId() != null) {
                ClassInfo classInfo = classInfoMapper.selectById(notice.getTargetId());
                if (classInfo != null) {
                    notice.setTargetName("班级通知 - " + classInfo.getClassName());
                } else {
                    notice.setTargetName("班级通知");
                }
            }
        });
        
        return result;
    }
}
```

### backend/src/main/java/com/student/growth/service/ScoreRecordService.java
```java
package com.student.growth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.ScoreRecord;
import com.student.growth.mapper.ScoreRecordMapper;
import org.springframework.stereotype.Service;

import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ScoreRecordService extends ServiceImpl<ScoreRecordMapper, ScoreRecord> {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public boolean save(ScoreRecord entity) {
        fillInfo(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(ScoreRecord entity) {
        fillInfo(entity);
        return super.updateById(entity);
    }

    private void fillInfo(ScoreRecord entity) {
        if (entity.getStudentId() != null) {
            StudentInfo student = studentInfoMapper.selectById(entity.getStudentId());
            if (student != null) {
                entity.setStudentName(student.getName());
                if (student.getClassName() != null) {
                    entity.setClassName(student.getClassName());
                } else if (student.getClassId() != null) {
                    ClassInfo classInfo = classInfoMapper.selectById(student.getClassId());
                    if (classInfo != null) {
                        entity.setClassName(classInfo.getClassName());
                    }
                }
            }
        }
    }
}
```

### backend/src/main/java/com/student/growth/service/StudentInfoService.java
```java
package com.student.growth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentInfoService extends ServiceImpl<StudentInfoMapper, StudentInfo> {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    public List<StudentInfo> getByTeacherId(Long teacherId) {
        // 1. 查询老师管理的班级
        List<ClassInfo> classes = classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getTeacherId, teacherId));
        if (classes.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> classIds = classes.stream().map(ClassInfo::getId).collect(Collectors.toList());
        
        // 2. 查询这些班级的学生
        return list(new LambdaQueryWrapper<StudentInfo>()
                .in(StudentInfo::getClassId, classIds));
    }

    @Override
    public boolean save(StudentInfo entity) {
        if (entity.getClassId() != null) {
            ClassInfo classInfo = classInfoMapper.selectById(entity.getClassId());
            if (classInfo != null) {
                entity.setClassName(classInfo.getClassName());
            }
        }
        return super.save(entity);
    }

    @Override
    public boolean updateById(StudentInfo entity) {
        if (entity.getClassId() != null) {
            ClassInfo classInfo = classInfoMapper.selectById(entity.getClassId());
            if (classInfo != null) {
                entity.setClassName(classInfo.getClassName());
            }
        }
        return super.updateById(entity);
    }

    public Page<StudentInfo> getStudentPage(Integer current, Integer size, String keyword) {
        Page<StudentInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(StudentInfo::getStudentNo, keyword)
                    .or()
                    .like(StudentInfo::getName, keyword));
        }
        wrapper.orderByDesc(StudentInfo::getCreateTime);
        Page<StudentInfo> result = page(page, wrapper);
        
        result.getRecords().forEach(student -> {
            // Only fill if className is missing (optimization for denormalized schema)
            if (student.getClassName() == null && student.getClassId() != null) {
                ClassInfo classInfo = classInfoMapper.selectById(student.getClassId());
                if (classInfo != null) {
                    student.setClassName(classInfo.getClassName());
                }
            }
        });
        
        return result;
    }

    public StudentInfo getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getUserId, userId));
    }

    public List<StudentInfo> getByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getParentId, parentId));
    }

    public void bindStudent(Long parentId, String studentNo) {
        StudentInfo student = getOne(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getStudentNo, studentNo));
        if (student == null) {
            throw new RuntimeException("学号不存在");
        }
        if (student.getParentId() != null) {
            throw new RuntimeException("该学生已被其他家长绑定");
        }
        student.setParentId(parentId);
        updateById(student);
    }

    public void unbindStudent(Long parentId, Long studentId) {
        StudentInfo student = getById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (!parentId.equals(student.getParentId())) {
            throw new RuntimeException("无权解绑该学生");
        }
        student.setParentId(null);
        updateById(student);
    }
}
```

### backend/src/main/java/com/student/growth/util/JwtUtil.java
```java
package com.student.growth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
```
