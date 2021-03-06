package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SkuEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:22
 */
public interface SkuService extends IService<SkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<String> querySkuImages(Long skuId);

}

