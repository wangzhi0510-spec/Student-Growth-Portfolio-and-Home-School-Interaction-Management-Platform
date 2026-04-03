package com.student.growth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comprehensive_evaluation")
public class ComprehensiveEvaluation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String studentName;
    private String className;
    private String semester;
    private Integer moralScore;
    private Integer intellectualScore;
    private Integer physicalScore;
    private Integer aestheticScore;
    private Integer laborScore;
    private Integer totalScore;
    private String level;
    private Long teacherId;
    private String remark;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
