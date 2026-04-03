
### backend/src/main/java/com/student/growth/controller/AuthController.java
```java
package com.student.growth.controller;

import com.student.growth.common.Result;
import com.student.growth.dto.LoginRequest;
import com.student.growth.dto.LoginResponse;
import com.student.growth.dto.RegisterRequest;
import com.student.growth.entity.User;
import com.student.growth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/current")
    public Result<User> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = authService.getCurrentUser(userId);
        user.setPassword(null);
        return Result.success(user);
    }
}
```

### backend/src/main/java/com/student/growth/controller/NoticeController.java
```java
package com.student.growth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.growth.common.Result;
import com.student.growth.entity.Notice;
import com.student.growth.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/page")
    public Result<Page<Notice>> getNoticePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Notice> page = noticeService.getNoticePage(current, size);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        return Result.success(notice);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> addNotice(@RequestBody Notice notice) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notice.setPublisherId(userId);
        noticeService.save(notice);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> updateNotice(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }
}
```

### backend/src/main/java/com/student/growth/controller/ScoreController.java
```java
package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.growth.common.Result;
import com.student.growth.entity.ScoreRecord;
import com.student.growth.service.ScoreRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreRecordService scoreRecordService;

    @GetMapping("/student/{studentId}")
    public Result<List<ScoreRecord>> getScoresByStudentId(@PathVariable Long studentId) {
        List<ScoreRecord> scores = scoreRecordService.list(new LambdaQueryWrapper<ScoreRecord>()
                .eq(ScoreRecord::getStudentId, studentId)
                .orderByDesc(ScoreRecord::getExamDate));
        return Result.success(scores);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<ScoreRecord>> getScorePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ScoreRecord> page = new Page<>(current, size);
        Page<ScoreRecord> result = scoreRecordService.page(page);
        return Result.success(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addScore(@RequestBody ScoreRecord score) {
        System.out.println("ScoreController - addScore called");
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        score.setTeacherId(userId);
        scoreRecordService.save(score);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateScore(@RequestBody ScoreRecord score) {
        scoreRecordService.updateById(score);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteScore(@PathVariable Long id) {
        scoreRecordService.removeById(id);
        return Result.success();
    }
}
```

### backend/src/main/java/com/student/growth/controller/StatisticsController.java
```java
package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.*;
import com.student.growth.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private ScoreRecordService scoreRecordService;

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 学生总数
        stats.put("studentCount", studentInfoService.count());
        
        // 教师总数
        stats.put("teacherCount", userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "TEACHER")));
                
        // 班级总数
        stats.put("classCount", classInfoService.count());
        
        // 消息总数
        stats.put("messageCount", messageService.count());
        
        return Result.success(stats);
    }

    @GetMapping("/score/{studentId}")
    public Result<Map<String, Object>> getScoreStatistics(@PathVariable Long studentId) {
        List<ScoreRecord> scores = scoreRecordService.list(new LambdaQueryWrapper<ScoreRecord>()
                .eq(ScoreRecord::getStudentId, studentId));

        Map<String, List<ScoreRecord>> scoresBySubject = scores.stream()
                .collect(Collectors.groupingBy(ScoreRecord::getSubject));

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<ScoreRecord>> entry : scoresBySubject.entrySet()) {
            String subject = entry.getKey();
            List<ScoreRecord> subjectScores = entry.getValue();
            double avgScore = subjectScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .average()
                    .orElse(0.0);
            double maxScore = subjectScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .max()
                    .orElse(0.0);
            double minScore = subjectScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .min()
                    .orElse(0.0);

            Map<String, Object> subjectStats = new HashMap<>();
            subjectStats.put("average", avgScore);
            subjectStats.put("max", maxScore);
            subjectStats.put("min", minScore);
            subjectStats.put("count", subjectScores.size());
            result.put(subject, subjectStats);
        }

        return Result.success(result);
    }

    @GetMapping("/score/trend/{studentId}")
    public Result<Map<String, Object>> getScoreTrend(
            @PathVariable Long studentId,
            @RequestParam(required = false) String subject) {
        LambdaQueryWrapper<ScoreRecord> wrapper = new LambdaQueryWrapper<ScoreRecord>()
                .eq(ScoreRecord::getStudentId, studentId)
                .orderByAsc(ScoreRecord::getExamDate);
        
        List<ScoreRecord> scores = scoreRecordService.list(wrapper);

        Map<String, Object> trend = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> scoreValues = new ArrayList<>();

        if (subject != null && !subject.isEmpty()) {
            // 单科趋势：直接过滤
            scores = scores.stream()
                    .filter(s -> s.getSubject().equals(subject))
                    .collect(Collectors.toList());
            for (ScoreRecord score : scores) {
                dates.add(score.getExamDate().toString());
                scoreValues.add(score.getScore().doubleValue());
            }
        } else {
            // 总成绩趋势：按日期聚合求和
            Map<String, Double> sumByDate = scores.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getExamDate().toString(),
                            Collectors.summingDouble(s -> s.getScore().doubleValue())
                    ));
            
            // 按日期排序
            dates = new ArrayList<>(sumByDate.keySet());
            Collections.sort(dates);
            
            for (String date : dates) {
                scoreValues.add(sumByDate.get(date));
            }
        }

        trend.put("dates", dates);
        trend.put("scores", scoreValues);

        return Result.success(trend);
    }
}
```

### backend/src/main/java/com/student/growth/controller/StudentController.java
```java
package com.student.growth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.growth.common.Result;
import com.student.growth.entity.StudentInfo;
import com.student.growth.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentInfoService studentInfoService;

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Page<StudentInfo>> getStudentPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<StudentInfo> page = studentInfoService.getStudentPage(current, size, keyword);
        return Result.success(page);
    }
    
    @GetMapping("/parent/my")
    @PreAuthorize("hasRole('PARENT')")
    public Result<List<StudentInfo>> getMyStudents() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(studentInfoService.getByParentId(userId));
    }

    @GetMapping("/teacher/my")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<List<StudentInfo>> getTeacherStudents() {
        Long teacherId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(studentInfoService.getByTeacherId(teacherId));
    }

    @GetMapping("/{id}")
    public Result<StudentInfo> getStudentById(@PathVariable Long id) {
        StudentInfo student = studentInfoService.getById(id);
        return Result.success(student);
    }

    @PostMapping("/bind")
    @PreAuthorize("hasRole('PARENT')")
    public Result<Void> bindStudent(@RequestParam String studentNo) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        studentInfoService.bindStudent(userId, studentNo);
        return Result.success();
    }

    @PostMapping("/unbind")
    @PreAuthorize("hasRole('PARENT')")
    public Result<Void> unbindStudent(@RequestParam Long studentId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        studentInfoService.unbindStudent(userId, studentId);
        return Result.success();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result<Void> addStudent(@RequestBody StudentInfo student) {
        System.out.println("StudentController - Entering addStudent method");
        studentInfoService.save(student);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateStudent(@RequestBody StudentInfo student) {
        studentInfoService.updateById(student);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        studentInfoService.removeById(id);
        return Result.success();
    }
}
```
