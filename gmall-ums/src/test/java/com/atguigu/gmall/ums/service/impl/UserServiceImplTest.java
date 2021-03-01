package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Author: Administrator
 * Create: 2021/3/1
 **/
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserService userService;

    @Test
    void sendValidateCode() {
        userService.sendValidateCode("13335894150", 2);
    }
    @Test
    void userRegister() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("leoanrdo");
        userEntity.setPassword("leonardo");
        userEntity.setEmail("leo@qq.com");
        userEntity.setPhone("13335894150");
        userService.userRegister(userEntity, "828820");
    }


}