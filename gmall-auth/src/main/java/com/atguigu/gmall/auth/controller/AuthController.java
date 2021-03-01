package com.atguigu.gmall.auth.controller;

import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.common.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
@Controller
@Slf4j
public class AuthController {

    @Autowired
    AuthService authService;

    /**
     * 去到登陆页面
     * @return 到登陆页面
     */
    @GetMapping("toLogin.html")
    public String toLogin(@RequestParam("returnUrl") String returnUrl, Model model) {

        // 把登录前的页面地址，记录到登录页面，以备将来登录成功，回到登录前的页面
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }

    /**
     * 登录请求
     * @return 重定向到原来的页面
     */
    @PostMapping("/login")
    public String login(@RequestParam(value = "returnUrl",defaultValue = "gmall.com") String returnUrl,
                        @RequestParam("loginName") String loginName,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        try {
            authService.accreditation(loginName, password, request, response);
        } catch (UserException e) {
            e.printStackTrace();
            log.warn("用户登陆失败！用户名为: {} \t 密码为: {}", loginName, password);
            throw new UserException("登录失败！用户名或密码有误");
            // return "login";
        }
        return "redirect:" + returnUrl;
    }

}
