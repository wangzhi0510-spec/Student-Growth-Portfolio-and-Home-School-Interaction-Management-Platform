package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.TeacherComment;
import com.student.growth.service.TeacherCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师评语控制器
 * 负责管理教师对学生的阶段性文字评语
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private TeacherCommentService commentService;

    /**
     * 根据学生ID查询所有评语
     * 权限：登录用户皆可查看（学生看自己的，家长看孩子的，老师看全班的）
     */
    @GetMapping("/student/{studentId}")
    public Result<List<TeacherComment>> getCommentsByStudentId(@PathVariable Long studentId) {
        List<TeacherComment> comments = commentService.list(new LambdaQueryWrapper<TeacherComment>()
                .eq(TeacherComment::getStudentId, studentId) // 匹配当前学生
                .orderByDesc(TeacherComment::getCommentDate)); // 按评价时间倒序排列，最新的在最前
        return Result.success(comments);
    }

    /**
     * 新增评语
     * 权限：只有管理员和教师可以写评语
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addComment(@RequestBody TeacherComment comment) {
        // 【安全核心】从 Spring Security 全局上下文中获取当前操作者的 ID (即发评语的教师ID)
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        comment.setTeacherId(userId);// 强制覆盖 teacherId，防止前端伪造其他老师的身份
        commentService.save(comment);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateComment(@RequestBody TeacherComment comment) {
        commentService.updateById(comment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success();
    }
}
