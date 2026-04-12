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
    // 分页获取当前用户收到的消息（收件箱）
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

    // 标记消息为已读并记录精准阅读时间
    public void markAsRead(Long messageId) {
        Message message = getById(messageId);
        if (message != null) {
            message.setIsRead(1);// 1表示已读
            message.setReadTime(LocalDateTime.now());// 记录发生阅读的具体时间
            updateById(message);
        }
    }

    public Long getUnreadCount(Long receiverId) {
        return count(new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, receiverId)
                .eq(Message::getIsRead, 0));
    }
}
