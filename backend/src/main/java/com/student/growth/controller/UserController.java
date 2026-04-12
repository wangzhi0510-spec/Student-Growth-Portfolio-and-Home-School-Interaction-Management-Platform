package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.growth.common.Result;
import com.student.growth.entity.StudentInfo;
import com.student.growth.entity.User;
import com.student.growth.service.StudentInfoService;
import com.student.growth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 获取合法教师列表逻辑（用于班级下拉选择框）
    @GetMapping("/list/teacher")
    public Result<List<User>> getTeacherList() {
        List<User> teachers = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "TEACHER")// 严格限定角色为教师
                .eq(User::getStatus, 1)      // 限定状态为1（已启用）
                .orderByDesc(User::getCreateTime));
        teachers.forEach(user -> user.setPassword(null));
        return Result.success(teachers);
    }

    // 用户分页查询与脱敏处理逻辑
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String role) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 动态判断前端是否传来了角色筛选条件
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(page, wrapper);
        // 数据脱敏：清空返回给前端的密码字段
        result.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Result<Void> addUser(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        String realName = (String) request.get("realName");
        String phone = (String) request.get("phone");
        String email = (String) request.get("email");
        String role = (String) request.get("role");
        Integer status = (Integer) request.get("status");
        String studentNo = (String) request.get("studentNo");

        User existUser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 验证家长注册时必须提供有效的学号
        if ("PARENT".equals(role)) {
            if (studentNo == null || studentNo.isEmpty()) {
                return Result.error("新增家长必须填写学生学号");
            }
            StudentInfo student = studentInfoService.getOne(new LambdaQueryWrapper<StudentInfo>()
                    .eq(StudentInfo::getStudentNo, studentNo));
            if (student == null) {
                return Result.error("学号不存在");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(status);
        userService.save(user);

        // 如果是家长注册，需要关联学生
        if ("PARENT".equals(role)) {
            StudentInfo student = studentInfoService.getOne(new LambdaQueryWrapper<StudentInfo>()
                    .eq(StudentInfo::getStudentNo, studentNo));
            student.setParentId(user.getId());
            studentInfoService.updateById(student);
        }

        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUser(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    // 用户个人资料更新逻辑
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        // 接收前端传来的只包含需要修改字段的 User 对象
        // 使用 MyBatis-Plus 的 updateById 进行动态更新
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}
