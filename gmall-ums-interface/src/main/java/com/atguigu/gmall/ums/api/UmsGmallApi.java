package com.atguigu.gmall.ums.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
public interface UmsGmallApi {

    @GetMapping("ums/user/query")
    public ResponseVo<String> userLogin(@RequestParam("loginName") String loginName, @RequestParam("password") String password);

}
