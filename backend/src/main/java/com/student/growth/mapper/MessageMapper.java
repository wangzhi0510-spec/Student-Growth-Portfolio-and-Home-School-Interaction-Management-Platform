package com.student.growth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.student.growth.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
