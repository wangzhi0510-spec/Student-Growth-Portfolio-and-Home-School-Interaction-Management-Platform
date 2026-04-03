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
