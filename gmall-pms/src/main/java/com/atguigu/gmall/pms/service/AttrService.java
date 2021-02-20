package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:23
 */
public interface AttrService extends IService<AttrEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    List<AttrEntity> getAttrOfCategory(Long cid, Integer type, Integer searchType);

    List<SkuAttrValueEntity> getSkuSearchTypeBySkuId(Long sid);

    List<SpuAttrValueEntity> getSpuSearchTypeBySpuId(Long sid);

}

