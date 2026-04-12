package com.student.growth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 层面的全局跨域访问配置
 * * 注意：因为项目中使用了 Spring Security，跨域请求会先经过 Security 的过滤器链。
 * 所以这里的配置主要作为 MVC 层面的基础/兜底配置。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 重写添加跨域映射的方法
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // "/**" 表示对所有的路径都进行跨域配置
        registry.addMapping("/**")
                // 允许所有来源（使用 allowedOriginPatterns 而不是 allowedOrigins 适配更广）
                .allowedOriginPatterns("*")
                // 这里设置为 false，如果不涉及带 Cookie 的跨域，设为 false 更安全简便
                .allowCredentials(false)
                // 允许前后端交互的 HTTP 动作
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                // 预检请求（OPTIONS）的缓存时间，单位为秒。3600秒内浏览器不需要再发送预检请求，提升性能
                .maxAge(3600);
    }

}