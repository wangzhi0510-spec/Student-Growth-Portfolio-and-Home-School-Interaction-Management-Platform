package com.student.growth.controller;

import com.student.growth.common.Result;
import com.student.growth.dto.LoginRequest;
import com.student.growth.dto.LoginResponse;
import com.student.growth.dto.RegisterRequest;
import com.student.growth.entity.User;
import com.student.growth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权控制器
 * 负责处理用户的登录、注册以及获取当前登录用户信息
 */
@RestController
@RequestMapping("/auth") // 该类所有接口的路径前缀均为 /auth
public class AuthController {

    @Autowired
    private AuthService authService; // 注入真正的业务逻辑执行者

    /**
     * 登录接口
     * @param request 包含用户名和密码的请求体 (@Valid 用于开启参数校验)
     * @return 返回包含 JWT Token 和用户基础信息的 LoginResponse
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // 调用 Service 层核对账号密码，核对成功会签发 Token
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            // 登录失败（如密码错误、账号被禁用），返回统一的错误信息格式
            return Result.error(e.getMessage());
        }
    }

    /**
     * 注册接口
     * @param request 包含注册所需信息的请求体
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 执行注册逻辑（包含家长与学生学号的绑定）
            authService.register(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * @param authentication 包含当前请求凭证的上下文对象（由 JWT 过滤器解析出来的）
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(Authentication authentication) {
        // 从凭证上下文中提取存入的 userId
        Long userId = (Long) authentication.getPrincipal();
        // 根据 ID 去数据库查询完整的用户实体
        User user = authService.getCurrentUser(userId);
        // 【关键安全操作】脱敏：在把用户信息返回给前端之前，必须抹除密码密文
        user.setPassword(null);
        return Result.success(user);
    }
}
