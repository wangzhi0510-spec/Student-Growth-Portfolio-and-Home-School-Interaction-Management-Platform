package com.student.growth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.student.growth.entity.Message;
import com.student.growth.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    public Page<Message> getReceivedMessages(Long receiverId, Integer current, Integer size) {
        Page<Message> page = new Page<>(current, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, receiverId)
                .orderByDesc(Message::getCreateTime);
        return page(page, wrapper);
    }

    public Page<Message> getSentMessages(Long senderId, Integer current, Integer size) {
        Page<Message> page = new Page<>(current, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, senderId)
                .orderByDesc(Message::getCreateTime);
        return page(page, wrapper);
    }

    public void markAsRead(Long messageId) {
        Message message = getById(messageId);
        if (message != null) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            updateById(message);
        }
    }

    public Long getUnreadCount(Long receiverId) {
        return count(new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, receiverId)
                .eq(Message::getIsRead, 0));
    }
}
