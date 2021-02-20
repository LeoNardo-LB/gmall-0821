package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import io.jsonwebtoken.lang.Collections;
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

import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;
import org.springframework.util.StringUtils;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, AttrEntity> implements AttrService {

    @Autowired
    AttrMapper attrMapper;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SpuAttrValueService spuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<AttrEntity> getAttrOfCategory(Long cid, Integer type, Integer searchType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", cid);

        // 
        if (type != null) {
            wrapper.eq("type", type);
        }
        if (searchType != null) {
            wrapper.eq("search_type", searchType);
        }

        return attrMapper.selectList(wrapper);
    }

    @Override
    public List<SkuAttrValueEntity> getSkuSearchTypeBySkuId(Long sid) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("search_type", 1);
        List<AttrEntity> attrEntities = attrMapper.selectList(wrapper);
        List<Long> idList = attrEntities.stream().map(attr -> {
            return attr.getId();
        }).collect(Collectors.toList());

        QueryWrapper<SkuAttrValueEntity> skuAttrWrapper = new QueryWrapper<>();
        skuAttrWrapper.eq("sku_id", sid).in("attr_id", idList);
        return skuAttrValueService.list(skuAttrWrapper);
    }

    @Override
    public List<SpuAttrValueEntity> getSpuSearchTypeBySpuId(Long sid) {
        QueryWrapper<AttrEntity> attrWrapper = new QueryWrapper<>();
        attrWrapper.eq("search_type", 1);
        List<AttrEntity> attrEntities = attrMapper.selectList(attrWrapper);
        List<Long> idList = attrEntities.stream().map(attr -> {
            return attr.getId();
        }).collect(Collectors.toList());

        QueryWrapper<SpuAttrValueEntity> spuAttrWrapper = new QueryWrapper<>();
        spuAttrWrapper.eq("spu_id", sid).in("attr_id", idList);
        return spuAttrValueService.list(spuAttrWrapper);
    }

}