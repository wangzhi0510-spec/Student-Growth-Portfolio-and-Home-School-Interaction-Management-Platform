package com.student.growth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.student.growth.mapper")
public class StudentGrowthSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentGrowthSystemApplication.class, args);
    }
}
