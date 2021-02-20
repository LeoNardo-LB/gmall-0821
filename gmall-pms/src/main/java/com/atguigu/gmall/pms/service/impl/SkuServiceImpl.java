package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.service.SkuImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.service.SkuService;

@Service("skuService")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuEntity> implements SkuService {

    @Autowired
    SkuImagesService skuImagesService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<String> querySkuImages(Long skuId) {
        List<SkuImagesEntity> skuImagesEntityList = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        List<String> collect = skuImagesEntityList.stream().map(item -> item.getUrl()).collect(Collectors.toList());
        return collect;
    }

}