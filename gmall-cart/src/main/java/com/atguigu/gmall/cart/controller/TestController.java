package com.atguigu.gmall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Administrator
 * Create: 2021/2/24
 **/
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        System.out.println("进入到【Test】 的 Handler方法中。");
        return "ok";
    }
}
