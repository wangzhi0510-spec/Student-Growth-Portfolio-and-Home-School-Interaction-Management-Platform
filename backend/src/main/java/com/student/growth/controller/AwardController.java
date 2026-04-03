package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.AwardRecord;
import com.student.growth.service.AwardRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/award")
public class AwardController {

    @Autowired
    private AwardRecordService awardRecordService;

    @GetMapping("/student/{studentId}")
    public Result<List<AwardRecord>> getAwardsByStudentId(@PathVariable Long studentId) {
        List<AwardRecord> awards = awardRecordService.list(new LambdaQueryWrapper<AwardRecord>()
                .eq(AwardRecord::getStudentId, studentId)
                .orderByDesc(AwardRecord::getAwardDate));
        return Result.success(awards);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addAward(@RequestBody AwardRecord award) {
        awardRecordService.save(award);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateAward(@RequestBody AwardRecord award) {
        awardRecordService.updateById(award);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteAward(@PathVariable Long id) {
        awardRecordService.removeById(id);
        return Result.success();
    }
}
