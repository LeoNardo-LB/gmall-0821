package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.order.feign.UmsFeignClient;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.UserInfo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/28
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    UmsFeignClient umsFeignClient;

    @Override
    public OrderConfirmVo confirm() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();
        if (userInfo == null || StringUtils.isBlank(userInfo.getUserId())) {
            return null;
        }

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        ResponseVo<List<UserAddressEntity>> userAddrRespVo = umsFeignClient.queryByUserId(userInfo.getUserId());
        List<UserAddressEntity> userAddressEntities = userAddrRespVo.getData();
        if(!CollectionUtils.isEmpty(userAddressEntities)){
            confirmVo.setAddresses(userAddressEntities);
        }
        // todo

        return null;
    }

}
