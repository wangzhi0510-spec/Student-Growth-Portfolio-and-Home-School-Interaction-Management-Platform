package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.AwardRecord;
import com.student.growth.service.AwardRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生获奖记录控制器
 * 负责获奖记录的增删改查
 */
@RestController
@RequestMapping("/award")
public class AwardController {

    @Autowired
    private AwardRecordService awardRecordService;

    /**
     * 根据学生ID查询该学生的所有获奖记录
     * 注意：没有写 @PreAuthorize，表示只要登录（含家长、学生）就能查看
     * @param studentId URL路径中的学生ID
     */
    @GetMapping("/student/{studentId}")
    public Result<List<AwardRecord>> getAwardsByStudentId(@PathVariable Long studentId) {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 构造 SQL 查询条件
        List<AwardRecord> awards = awardRecordService.list(new LambdaQueryWrapper<AwardRecord>()
                .eq(AwardRecord::getStudentId, studentId) // WHERE student_id = ?
                .orderByDesc(AwardRecord::getAwardDate)); // ORDER BY award_date DESC (按日期倒序)
        return Result.success(awards);
    }

    /**
     * 新增获奖记录
     * 权限要求：仅限具有 ADMIN(管理员) 或 TEACHER(教师) 角色的人员操作
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> addAward(@RequestBody AwardRecord award) {
        // Mybatis-Plus 提供的通用保存方法，自动生成 INSERT 语句
        awardRecordService.save(award);
        return Result.success();
    }

    /**
     * 修改获奖记录
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> updateAward(@RequestBody AwardRecord award) {
        // Mybatis-Plus 提供的通用更新方法，根据传入对象的 ID 自动生成 UPDATE 语句
        awardRecordService.updateById(award);
        return Result.success();
    }

    /**
     * 删除指定的获奖记录
     * @param id 获奖记录的唯一标识 ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteAward(@PathVariable Long id) {
        // 自动生成 DELETE 语句
        awardRecordService.removeById(id);
        return Result.success();
    }
}
