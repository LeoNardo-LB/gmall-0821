package com.atguigu.gmall.order.interceptor;

import com.atguigu.gmall.order.vo.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Administrator
 * Create: 2021/2/28
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<UserInfo> USER_INFO_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfo userInfo = new UserInfo();
        String userId = request.getHeader("userId");
        userInfo.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        USER_INFO_THREAD_LOCAL.remove();
    }

    public static UserInfo getUserInfo() {
        return USER_INFO_THREAD_LOCAL.get();
    }

}
