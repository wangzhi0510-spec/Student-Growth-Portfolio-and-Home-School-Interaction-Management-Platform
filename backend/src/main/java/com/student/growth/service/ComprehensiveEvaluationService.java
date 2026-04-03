package com.student.growth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.ComprehensiveEvaluation;
import com.student.growth.mapper.ComprehensiveEvaluationMapper;
import org.springframework.stereotype.Service;

import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ComprehensiveEvaluationService extends ServiceImpl<ComprehensiveEvaluationMapper, ComprehensiveEvaluation> {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public boolean save(ComprehensiveEvaluation entity) {
        fillInfo(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(ComprehensiveEvaluation entity) {
        fillInfo(entity);
        return super.updateById(entity);
    }

    private void fillInfo(ComprehensiveEvaluation entity) {
        if (entity.getStudentId() != null) {
            StudentInfo student = studentInfoMapper.selectById(entity.getStudentId());
            if (student != null) {
                entity.setStudentName(student.getName());
                if (student.getClassName() != null) {
                    entity.setClassName(student.getClassName());
                } else if (student.getClassId() != null) {
                    ClassInfo classInfo = classInfoMapper.selectById(student.getClassId());
                    if (classInfo != null) {
                        entity.setClassName(classInfo.getClassName());
                    }
                }
            }
        }
    }
}
