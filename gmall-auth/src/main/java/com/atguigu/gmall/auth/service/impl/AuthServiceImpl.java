package com.atguigu.gmall.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.auth.autoconfig.AuthProperties;
import com.atguigu.gmall.auth.feign.UmsFeignClient;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.exception.UserException;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UmsFeignClient umsFeignClient;

    @Autowired
    AuthProperties authProperties;

    @Override
    public void accreditation(String loginName, String password, HttpServletRequest request, HttpServletResponse response) {
        // 验证用户
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(password)) {
            throw new UserException("用户名或密码不能为空！");
        }
        ResponseVo<String> userLoginResponseVo = umsFeignClient.userLogin(loginName, password);
        String userJson = userLoginResponseVo.getData();
        UserEntity userEntity = JSON.parseObject(userJson, UserEntity.class);
        if (userEntity == null) {
            throw new UserException("用户名或密码有误！");
        }

        // 制作载荷
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userEntity.getId());
        map.put("username", userEntity.getUsername());
        map.put("ip", IpUtil.getIpAddressAtService(request));

        // 制作jwt令牌
        String jwt = null;
        try {
            jwt = JwtUtils.generateToken(map, authProperties.getPrivateKey(), authProperties.getExpire() * 60);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserException("生成jwt令牌出错！");
        }

        // 设置到Cookie中
        CookieUtils.setCookie(request, response, authProperties.getCookieName(), jwt, authProperties.getExpire() * 60);
        CookieUtils.setCookie(request, response, authProperties.getUnick(), userEntity.getUsername(), authProperties.getExpire() * 60);
    }

}
