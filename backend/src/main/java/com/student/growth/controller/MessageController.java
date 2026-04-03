package com.student.growth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.growth.common.Result;
import com.student.growth.entity.Message;
import com.student.growth.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/received")
    public Result<Page<Message>> getReceivedMessages(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<Message> page = messageService.getReceivedMessages(userId, current, size);
        return Result.success(page);
    }

    @GetMapping("/sent")
    public Result<Page<Message>> getSentMessages(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<Message> page = messageService.getSentMessages(userId, current, size);
        return Result.success(page);
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PostMapping
    public Result<Void> sendMessage(@RequestBody Message message, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        message.setSenderId(userId);
        messageService.save(message);
        return Result.success();
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        messageService.removeById(id);
        return Result.success();
    }
}
