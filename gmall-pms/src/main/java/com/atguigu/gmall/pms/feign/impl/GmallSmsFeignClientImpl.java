package com.atguigu.gmall.pms.feign.impl;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.controller.Dto.SmsSaveDto;
import com.atguigu.gmall.pms.feign.GmallSmsFeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/1/21
 **/
@Service
public class GmallSmsFeignClientImpl implements GmallSmsFeignClient {

    @Override
    public ResponseVo saveSkuBounds(SmsSaveDto smsSaveDto) {

        return ResponseVo.fail("调用远程接口失败！");
    }

    @Override
    public ResponseVo<List<ItemSaleVo>> getSaleStrategyBySkuId(Long skuId) {
        return ResponseVo.fail("调用远程接口失败！");
    }

}
