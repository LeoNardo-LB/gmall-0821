package com.atguigu.gmall.cart.interceptor;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.autoconfig.JwtProperties;
import com.atguigu.gmall.cart.entity.UserInfo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.Cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

/**
 * Author: Administrator
 * Create: 2021/2/24
 **/
@Component
@EnableConfigurationProperties({JwtProperties.class})
public class LoginInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<UserInfo> THREAD_LOCAL_USER_INFO = new ThreadLocal();

    @Autowired
    JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfo userInfo = new UserInfo();

        // 从cookie中获取userKey
        String userKey = CookieUtils.getCookieValue(request, jwtProperties.getUserKey());
        // 如果为空，则生成一个
        if (StringUtils.isBlank(userKey)) {
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request, response, jwtProperties.getUserKey(), userKey, jwtProperties.getExpireTime() * 60);
        }
        // 设置userKey
        userInfo.setUserKey(userKey);

        // 尝试获取 jwt
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        if (!StringUtils.isBlank(token)) {
            Map<String, Object> jwtLoadMap = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            String userId = jwtLoadMap.get("userId").toString();
            if (!StringUtils.isBlank(userId)) {
                userInfo.setUserId(userId);
            }
        }

        // 设置给userInfo
        THREAD_LOCAL_USER_INFO.set(userInfo);

        return true;
    }

    public static UserInfo getUserInfo() {
        return THREAD_LOCAL_USER_INFO.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL_USER_INFO.remove();
    }

}
