package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.ClassInfo;
import com.student.growth.service.ClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级信息控制器
 * 负责学校行政班级的创建与管理
 */
@RestController
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private ClassInfoService classInfoService;

    /**
     * 获取全校所有的班级列表
     * 权限：所有人（由于下拉框等需要绑定班级，所以开放给登录用户）
     */
    @GetMapping("/list")
    public Result<List<ClassInfo>> getClassList() {
        // 查询所有班级，并按照创建时间倒序排列
        List<ClassInfo> classes = classInfoService.list(new LambdaQueryWrapper<ClassInfo>()
                .orderByDesc(ClassInfo::getCreateTime));
        return Result.success(classes);
    }

    /**
     * 根据班级ID获取单个班级的详细信息
     */
    @GetMapping("/{id}")
    public Result<ClassInfo> getClassById(@PathVariable Long id) {
        ClassInfo classInfo = classInfoService.getById(id);
        return Result.success(classInfo);
    }

    /**
     * 新增班级
     * 权限要求：极其严格，仅限系统管理员（ADMIN）操作，防止教师越权建班
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addClass(@RequestBody ClassInfo classInfo) {
        classInfoService.save(classInfo);
        return Result.success();
    }

    /**
     * 更新班级信息（如更换班主任等）
     * 权限要求：仅限管理员
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateClass(@RequestBody ClassInfo classInfo) {
        classInfoService.updateById(classInfo);
        return Result.success();
    }

    /**
     * 删除班级
     * 权限要求：仅限管理员
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteClass(@PathVariable Long id) {
        // 注意：实际工程中，删除班级前通常需要校验该班级下是否还有学生，这里调用的是基础的物理删除或逻辑删除
        classInfoService.removeById(id);
        return Result.success();
    }
}
