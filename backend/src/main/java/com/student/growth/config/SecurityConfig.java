package com.student.growth.config;

import com.student.growth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 核心安全配置类
 * 负责全局的权限拦截、密码加密策略、跨域以及 JWT 过滤器的接入
 */
@Configuration
@EnableWebSecurity // 开启 Spring Security 的 Web 安全支持
@EnableMethodSecurity // 开启基于方法的安全控制（允许在Controller方法上写 @PreAuthorize("hasRole('ADMIN')")）
public class SecurityConfig {

    // 注入我们自定义的 JWT 身份验证过滤器
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 配置密码加密器
     * 使用 BCrypt 强哈希算法，保证数据库中的密码密文安全性
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 暴露 AuthenticationManager 实例
     * 用于在 AuthService 中手动调用，完成用户名密码的校验
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 核心安全过滤链配置（保安检查流水线）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF（跨站请求伪造）防护：因为我们使用了 JWT 代替 Cookie，天然免疫 CSRF 攻击
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 开启 CORS（跨域资源共享）：使用下方自定义的 corsConfigurationSource 规则
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 3. 设置 Session 管理策略为“无状态”(STATELESS)：不创建/使用后端 Session，所有请求全靠携带 JWT Token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. 配置 HTTP 请求的权限规则
                .authorizeHttpRequests(auth -> auth
                        // 对 "/auth/**" 路径下的所有接口放行（登录、注册不需要权限）
                        .requestMatchers("/auth/**").permitAll()
                        // 其他所有请求都必须经过身份认证（必须带有效的 Token）
                        .anyRequest().authenticated()
                )
                // 5. 将自定义的 JWT 过滤器插队到 Spring Security 默认的账密认证过滤器之前
                // 这样每次请求都会先解析 Token，如果 Token 有效，就告诉 Security 该用户已登录
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Spring Security 层面的跨域配置源
     * 解决前端 Vue (如 localhost:5173) 访问后端 (如 localhost:8080) 被浏览器拦截的问题
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有域名的前端发起请求（生产环境中建议替换为真实的前端域名，如 "http://www.yourschool.com"）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许的 HTTP 请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许前端携带所有的请求头信息（比如 Authorization: Bearer <token>）
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许前端携带凭证（如 Cookie），如果设置为 true，allowedOriginPatterns 不能为单纯的 "*"
        configuration.setAllowCredentials(true);
        // 将上述配置应用到所有的接口路径 "/**"
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
        
    }
}
 