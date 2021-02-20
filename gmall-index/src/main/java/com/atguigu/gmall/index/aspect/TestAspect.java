package com.atguigu.gmall.index.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Author: Administrator
 * Create: 2021/2/14
 **/
@Component
@Aspect
public class TestAspect {

    @Before("@annotation(com.atguigu.gmall.index.annotation.TestAnnotation)")
    public void test() {
        System.out.println("进入前置通知方法");
    }

}
