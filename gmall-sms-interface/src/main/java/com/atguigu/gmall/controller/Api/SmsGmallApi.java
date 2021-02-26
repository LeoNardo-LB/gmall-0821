package com.atguigu.gmall.controller.Api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.controller.Dto.SmsSaveDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/1/20
 **/
public interface SmsGmallApi {

    @PostMapping("sms/skubounds/save")
    public ResponseVo saveSkuBounds(@RequestBody SmsSaveDto smsSaveDto);

    /**
     * 根据skuId查询sku的所有营销信息
     * @param skuId
     * @return
     */
    @GetMapping("sms/skubounds/sale/strategy/{skuId}")
    public ResponseVo<List<ItemSaleVo>> getSaleStrategyBySkuId(@PathVariable Long skuId);

}
