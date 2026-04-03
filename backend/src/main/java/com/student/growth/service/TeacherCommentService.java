package com.student.growth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.TeacherComment;
import com.student.growth.mapper.TeacherCommentMapper;
import org.springframework.stereotype.Service;

import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TeacherCommentService extends ServiceImpl<TeacherCommentMapper, TeacherComment> {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public boolean save(TeacherComment entity) {
        fillInfo(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(TeacherComment entity) {
        fillInfo(entity);
        return super.updateById(entity);
    }

    private void fillInfo(TeacherComment entity) {
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
