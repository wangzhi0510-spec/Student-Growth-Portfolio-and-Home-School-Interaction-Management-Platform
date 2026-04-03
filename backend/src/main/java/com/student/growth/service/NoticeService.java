package com.student.growth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.ClassInfo;
import com.student.growth.entity.Notice;
import com.student.growth.entity.StudentInfo;
import com.student.growth.mapper.ClassInfoMapper;
import com.student.growth.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    @Autowired
    private StudentInfoService studentInfoService;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    public Page<Notice> getNoticePage(Integer current, Integer size) {
        Page<Notice> page = new Page<>(current, size);
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        // 获取当前用户绑定的学生列表（如果是家长）
        List<StudentInfo> students = studentInfoService.getByParentId(userId);
        List<Long> classIds = students.stream()
                .map(StudentInfo::getClassId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        wrapper.eq(Notice::getType, "school")
               .or(w -> w.eq(Notice::getType, "class").in(classIds.size() > 0, Notice::getTargetId, classIds))
               .orderByDesc(Notice::getPublishTime);
               
        Page<Notice> result = page(page, wrapper);
        
        // 填充目标名称
        result.getRecords().forEach(notice -> {
            if ("school".equals(notice.getType())) {
                notice.setTargetName("学校通知");
            } else if ("class".equals(notice.getType()) && notice.getTargetId() != null) {
                ClassInfo classInfo = classInfoMapper.selectById(notice.getTargetId());
                if (classInfo != null) {
                    notice.setTargetName("班级通知 - " + classInfo.getClassName());
                } else {
                    notice.setTargetName("班级通知");
                }
            }
        });
        
        return result;
    }
}
