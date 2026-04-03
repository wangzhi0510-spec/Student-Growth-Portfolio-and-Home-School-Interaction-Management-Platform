package com.student.growth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.student.growth.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
