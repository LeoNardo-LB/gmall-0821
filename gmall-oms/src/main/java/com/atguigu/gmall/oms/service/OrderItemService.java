package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.oms.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 19:31:36
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

