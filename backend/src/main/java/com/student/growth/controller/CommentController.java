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

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private TeacherCommentService commentService;

    @GetMapping("/student/{studentId}")
    public Result<List<TeacherComment>> getCommentsByStudentId(@PathVariable Long studentId) {
        List<TeacherComment> comments = commentService.list(new LambdaQueryWrapper<TeacherComment>()
                .eq(TeacherComment::getStudentId, studentId)
                .orderByDesc(TeacherComment::getCommentDate));
        return Result.success(comments);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addComment(@RequestBody TeacherComment comment) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        comment.setTeacherId(userId);
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
