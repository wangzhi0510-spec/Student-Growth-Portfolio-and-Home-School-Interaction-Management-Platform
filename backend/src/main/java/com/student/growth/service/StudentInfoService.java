package com.student.growth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.StudentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentInfoService extends ServiceImpl<StudentInfoMapper, StudentInfo> {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    public List<StudentInfo> getByTeacherId(Long teacherId) {
        // 1. 查询老师管理的班级
        List<ClassInfo> classes = classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getTeacherId, teacherId));
        if (classes.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> classIds = classes.stream().map(ClassInfo::getId).collect(Collectors.toList());
        
        // 2. 查询这些班级的学生
        return list(new LambdaQueryWrapper<StudentInfo>()
                .in(StudentInfo::getClassId, classIds));
    }

    @Override
    public boolean save(StudentInfo entity) {
        if (entity.getClassId() != null) {
            ClassInfo classInfo = classInfoMapper.selectById(entity.getClassId());
            if (classInfo != null) {
                entity.setClassName(classInfo.getClassName());
            }
        }
        return super.save(entity);
    }

    @Override
    public boolean updateById(StudentInfo entity) {
        if (entity.getClassId() != null) {
            ClassInfo classInfo = classInfoMapper.selectById(entity.getClassId());
            if (classInfo != null) {
                entity.setClassName(classInfo.getClassName());
            }
        }
        return super.updateById(entity);
    }

    public Page<StudentInfo> getStudentPage(Integer current, Integer size, String keyword) {
        Page<StudentInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(StudentInfo::getStudentNo, keyword)
                    .or()
                    .like(StudentInfo::getName, keyword));
        }
        wrapper.orderByDesc(StudentInfo::getCreateTime);
        Page<StudentInfo> result = page(page, wrapper);
        // 性能优化重难点：在内存中遍历回填 className，避免数据库全表 JOIN 锁表风险
        result.getRecords().forEach(student -> {
            // Only fill if className is missing (optimization for denormalized schema)
            if (student.getClassName() == null && student.getClassId() != null) {
                ClassInfo classInfo = classInfoMapper.selectById(student.getClassId());
                if (classInfo != null) {
                    student.setClassName(classInfo.getClassName());
                }
            }
        });
        
        return result;
    }

    public StudentInfo getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getUserId, userId));
    }

    public List<StudentInfo> getByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getParentId, parentId));
    }

    public void bindStudent(Long parentId, String studentNo) {
        StudentInfo student = getOne(new LambdaQueryWrapper<StudentInfo>()
                .eq(StudentInfo::getStudentNo, studentNo));
        if (student == null) {
            throw new RuntimeException("学号不存在");
        }
        if (student.getParentId() != null) {
            throw new RuntimeException("该学生已被其他家长绑定");
        }
        student.setParentId(parentId);
        updateById(student);
    }

    public void unbindStudent(Long parentId, Long studentId) {
        StudentInfo student = getById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        if (!parentId.equals(student.getParentId())) {
            throw new RuntimeException("无权解绑该学生");
        }
        student.setParentId(null);
        updateById(student);
    }
}
