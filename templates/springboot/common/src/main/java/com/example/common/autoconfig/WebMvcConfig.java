package com.example.common.autoconfig;

import com.example.common.handler.GlobalExceptionHandler;
import com.example.common.interceptor.JwtInterceptor;
import com.example.common.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(WebMvcConfigurer.class)
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtProperties jwtProperties;

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("添加JWT拦截器");
        registry.addInterceptor(new JwtInterceptor(jwtProperties))
                // 拦截所有路径
                .addPathPatterns("/**")
                // 排除路径
                .excludePathPatterns(
                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error"
                )
                .excludePathPatterns(
                        "/**/auth/**",
                        "/**/captcha/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保静态资源能够被正确映射
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 配置跨域请求
     *
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("添加CORS配置");
        registry.addMapping("/**")  // 允许所有路径
                .allowedOriginPatterns("*")  // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的请求方法
                .allowedHeaders("*")  // 允许的请求头
                .maxAge(3600);  // 预检请求的有效期，单位：秒
    }

    /**
     * 注册全局异常处理器
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        log.info("注册全局异常处理器");
        return new GlobalExceptionHandler();
    }
}
