package com.example.common.interceptor;

import com.example.common.context.UserContext;
import com.example.common.properties.JwtProperties;
import com.example.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * jwt令牌校验的拦截器
 */
@Slf4j
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是Controller的方法，直接放行
            return true;
        }

        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        // 校验令牌
        try {
            log.info("jwt校验: {}", token);
            Long Id = JwtUtil.parseJWT(token, jwtProperties);
            log.info("当前用户id: {}", Id);
            UserContext.setId(Id);
            // 通过，放行
            return true;
        } catch (Exception e) {
            // 不通过，响应 401
            log.error("jwt校验失败");
            response.setStatus(401);
            response.getWriter().write("jwt校验失败");
            response.getWriter().flush();
            return false;
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户信息
        UserContext.removeId();
    }
}
