package com.atguigu.gmall.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
public interface AuthService {

    void accreditation(String loginName, String password, HttpServletRequest request, HttpServletResponse response);

}
