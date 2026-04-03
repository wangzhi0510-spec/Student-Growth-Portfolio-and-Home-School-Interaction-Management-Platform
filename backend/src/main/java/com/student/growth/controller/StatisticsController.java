package com.student.growth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.student.growth.common.Result;
import com.student.growth.entity.*;
import com.student.growth.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private ScoreRecordService scoreRecordService;

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 学生总数
        stats.put("studentCount", studentInfoService.count());
        
        // 教师总数
        stats.put("teacherCount", userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "TEACHER")));
                
        // 班级总数
        stats.put("classCount", classInfoService.count());
        
        // 消息总数
        stats.put("messageCount", messageService.count());
        
        return Result.success(stats);
    }

    @GetMapping("/score/{studentId}")
    public Result<Map<String, Object>> getScoreStatistics(@PathVariable Long studentId) {
        List<ScoreRecord> scores = scoreRecordService.list(new LambdaQueryWrapper<ScoreRecord>()
                .eq(ScoreRecord::getStudentId, studentId));

        Map<String, List<ScoreRecord>> scoresBySubject = scores.stream()
                .collect(Collectors.groupingBy(ScoreRecord::getSubject));

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<ScoreRecord>> entry : scoresBySubject.entrySet()) {
            String subject = entry.getKey();
            List<ScoreRecord> subjectScores = entry.getValue();
            double avgScore = subjectScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .average()
                    .orElse(0.0);
            double maxScore = subjectScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .max()
                    .orElse(0.0);
            double minScore = subjectScores.stream()
                    .mapToDouble(s -> s.getScore().doubleValue())
                    .min()
                    .orElse(0.0);

            Map<String, Object> subjectStats = new HashMap<>();
            subjectStats.put("average", avgScore);
            subjectStats.put("max", maxScore);
            subjectStats.put("min", minScore);
            subjectStats.put("count", subjectScores.size());
            result.put(subject, subjectStats);
        }

        return Result.success(result);
    }

    @GetMapping("/score/trend/{studentId}")
    public Result<Map<String, Object>> getScoreTrend(
            @PathVariable Long studentId,
            @RequestParam(required = false) String subject) {
        LambdaQueryWrapper<ScoreRecord> wrapper = new LambdaQueryWrapper<ScoreRecord>()
                .eq(ScoreRecord::getStudentId, studentId)
                .orderByAsc(ScoreRecord::getExamDate);
        
        List<ScoreRecord> scores = scoreRecordService.list(wrapper);

        Map<String, Object> trend = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> scoreValues = new ArrayList<>();

        if (subject != null && !subject.isEmpty()) {
            // 单科趋势：直接过滤
            scores = scores.stream()
                    .filter(s -> s.getSubject().equals(subject))
                    .collect(Collectors.toList());
            for (ScoreRecord score : scores) {
                dates.add(score.getExamDate().toString());
                scoreValues.add(score.getScore().doubleValue());
            }
        } else {
            // 总成绩趋势：按日期聚合求和
            Map<String, Double> sumByDate = scores.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getExamDate().toString(),
                            Collectors.summingDouble(s -> s.getScore().doubleValue())
                    ));
            
            // 按日期排序
            dates = new ArrayList<>(sumByDate.keySet());
            Collections.sort(dates);
            
            for (String date : dates) {
                scoreValues.add(sumByDate.get(date));
            }
        }

        trend.put("dates", dates);
        trend.put("scores", scoreValues);

        return Result.success(trend);
    }
}
