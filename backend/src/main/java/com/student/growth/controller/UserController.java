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

    @GetMapping("/list/teacher")
    public Result<List<User>> getTeacherList() {
        List<User> teachers = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "TEACHER")
                .eq(User::getStatus, 1)
                .orderByDesc(User::getCreateTime));
        teachers.forEach(user -> user.setPassword(null));
        return Result.success(teachers);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String role) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (role != null && !role.isEmpty()) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userService.page(page, wrapper);
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

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
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
