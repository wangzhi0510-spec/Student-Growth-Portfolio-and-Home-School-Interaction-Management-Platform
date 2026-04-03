package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.ClassInfo;
import com.student.growth.service.ClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private ClassInfoService classInfoService;

    @GetMapping("/list")
    public Result<List<ClassInfo>> getClassList() {
        List<ClassInfo> classes = classInfoService.list(new LambdaQueryWrapper<ClassInfo>()
                .orderByDesc(ClassInfo::getCreateTime));
        return Result.success(classes);
    }

    @GetMapping("/{id}")
    public Result<ClassInfo> getClassById(@PathVariable Long id) {
        ClassInfo classInfo = classInfoService.getById(id);
        return Result.success(classInfo);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addClass(@RequestBody ClassInfo classInfo) {
        classInfoService.save(classInfo);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateClass(@RequestBody ClassInfo classInfo) {
        classInfoService.updateById(classInfo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteClass(@PathVariable Long id) {
        classInfoService.removeById(id);
        return Result.success();
    }
}
