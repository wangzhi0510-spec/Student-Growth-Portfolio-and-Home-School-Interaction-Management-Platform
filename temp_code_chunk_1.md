
### backend/pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.student</groupId>
    <artifactId>student-growth-system</artifactId>
    <version>1.0.0</version>
    <name>Student Growth System</name>
    <description>学生成长档案与家校互动管理平台</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <jwt.version>0.12.3</jwt.version>
        <hutool.version>5.8.24</hutool.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### backend/src/main/resources/application.yml
```yaml
spring:
  application:
    name: student-growth-system
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/student_growth?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: student-growth-system-secret-key-2024
  expiration: 86400000

server:
  port: 8080
  servlet:
    context-path: /api
```

### backend/src/main/resources/schema.sql
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_growth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_growth;

-- 用户表
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：ADMIN/TEACHER/STUDENT/PARENT',
  `avatar` VARCHAR(255) COMMENT '头像',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学生信息表
CREATE TABLE `student_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` int  NULL COMMENT '用户ID',
  `name` VARCHAR(50) DEFAULT NULL COMMENT '学生姓名',
  `student_no` VARCHAR(50) NOT NULL COMMENT '学号',
  `class_id` BIGINT COMMENT '班级ID',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级名称',
  `gender` VARCHAR(10) COMMENT '性别',
  `birthday` DATE COMMENT '生日',
  `id_card` VARCHAR(18) COMMENT '身份证号',
  `address` VARCHAR(255) COMMENT '家庭住址',
  `parent_id` BIGINT COMMENT '家长用户ID',
  `enrollment_date` DATE COMMENT '入学日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 班级表
CREATE TABLE `class_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
  `grade` VARCHAR(20) NOT NULL COMMENT '年级',
  `teacher_id` BIGINT COMMENT '班主任ID',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 成绩记录表
CREATE TABLE `score_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_name` VARCHAR(50) DEFAULT NULL COMMENT '学生姓名',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级名称',
  `subject` VARCHAR(50) NOT NULL COMMENT '科目',
  `score` DECIMAL(5,2) NOT NULL COMMENT '分数',
  `exam_type` VARCHAR(50) NOT NULL COMMENT '考试类型',
  `exam_date` DATE NOT NULL COMMENT '考试日期',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期',
  `teacher_id` BIGINT NOT NULL COMMENT '任课教师ID',
  `remark` VARCHAR(500) COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩记录表';

-- 综合素质评价表
CREATE TABLE `comprehensive_evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_name` VARCHAR(50) DEFAULT NULL COMMENT '学生姓名',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级名称',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期',
  `moral_score` INT DEFAULT 0 COMMENT '德育得分',
  `intellectual_score` INT DEFAULT 0 COMMENT '智育得分',
  `physical_score` INT DEFAULT 0 COMMENT '体育得分',
  `aesthetic_score` INT DEFAULT 0 COMMENT '美育得分',
  `labor_score` INT DEFAULT 0 COMMENT '劳育得分',
  `total_score` INT DEFAULT 0 COMMENT '总分',
  `level` VARCHAR(20) COMMENT '等级：优秀/良好/合格/待改进',
  `teacher_id` BIGINT NOT NULL COMMENT '评价教师ID',
  `remark` TEXT COMMENT '综合评语',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='综合素质评价表';

-- 教师评语表
CREATE TABLE `teacher_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评语ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_name` VARCHAR(50) DEFAULT NULL COMMENT '学生姓名',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级名称',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `comment_type` VARCHAR(20) NOT NULL COMMENT '评语类型：daily/monthly/term',
  `content` TEXT NOT NULL COMMENT '评语内容',
  `comment_date` DATE NOT NULL COMMENT '评语日期',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师评语表';

-- 获奖记录表
CREATE TABLE `award_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '获奖ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `student_name` VARCHAR(50) DEFAULT NULL COMMENT '学生姓名',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级名称',
  `award_name` VARCHAR(100) NOT NULL COMMENT '奖项名称',
  `award_level` VARCHAR(50) NOT NULL COMMENT '奖项级别：国家级/省级/市级/校级',
  `award_date` DATE NOT NULL COMMENT '获奖日期',
  `issuer` VARCHAR(100) COMMENT '颁发机构',
  `certificate_url` VARCHAR(255) COMMENT '证书图片URL',
  `remark` VARCHAR(500) COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='获奖记录表';

-- 家校互动留言表
CREATE TABLE `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '留言ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
  `read_time` DATETIME COMMENT '阅读时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家校互动留言表';

-- 通知公告表
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `type` VARCHAR(20) NOT NULL COMMENT '类型：school/class/personal',
  `target_type` VARCHAR(20) COMMENT '目标类型：all/student/teacher/parent',
  `target_id` BIGINT COMMENT '目标ID',
  `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
  `publish_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级：1-普通 2-重要 3-紧急',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 通知阅读记录表
CREATE TABLE `notice_read` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `notice_id` BIGINT NOT NULL COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知阅读记录表';

-- 角色权限表
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(200) COMMENT '描述',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限表';

-- 插入初始角色数据
INSERT INTO `sys_role` (`role_code`, `role_name`, `description`) VALUES
('ADMIN', '管理员', '系统管理员'),
('TEACHER', '教师', '教师角色'),
('STUDENT', '学生', '学生角色'),
('PARENT', '家长', '家长角色');

-- 插入默认管理员账号
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 'admin@school.com', 'ADMIN', 1);
```

### backend/src/main/java/com/student/growth/entity/User.java
```java
package com.student.growth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String role;
    private String avatar;
    private Integer status;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```
