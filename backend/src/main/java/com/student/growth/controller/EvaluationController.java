package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.ComprehensiveEvaluation;
import com.student.growth.service.ComprehensiveEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private ComprehensiveEvaluationService evaluationService;

    @GetMapping("/student/{studentId}")
    public Result<List<ComprehensiveEvaluation>> getEvaluationsByStudentId(@PathVariable Long studentId) {
        List<ComprehensiveEvaluation> evaluations = evaluationService.list(new LambdaQueryWrapper<ComprehensiveEvaluation>()
                .eq(ComprehensiveEvaluation::getStudentId, studentId)
                .orderByDesc(ComprehensiveEvaluation::getCreateTime));
        return Result.success(evaluations);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addEvaluation(@RequestBody ComprehensiveEvaluation evaluation) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        evaluation.setTeacherId(userId);
        evaluationService.save(evaluation);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateEvaluation(@RequestBody ComprehensiveEvaluation evaluation) {
        evaluationService.updateById(evaluation);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteEvaluation(@PathVariable Long id) {
        evaluationService.removeById(id);
        return Result.success();
    }
}
