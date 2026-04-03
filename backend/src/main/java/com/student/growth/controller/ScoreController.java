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
