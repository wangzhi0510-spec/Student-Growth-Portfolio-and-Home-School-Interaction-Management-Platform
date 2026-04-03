package com.student.growth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("student_info")
public class StudentInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String studentNo;
    private Long classId;
    private String className;
    private String gender;
    private LocalDate birthday;
    private String idCard;
    private String address;
    private Long parentId;
    private LocalDate enrollmentDate;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
