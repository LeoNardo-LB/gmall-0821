package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SaleAttrValueVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Autowired
    SkuService skuService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SaleAttrValueVo> querySkuValueBySpuId(Long spuId) {

        List<SaleAttrValueVo> saleAttrValueVos = new ArrayList<>();

        // 根据spuId获取SkuEntities
        List<SkuEntity> skuEntities = skuService.list(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        // 从SkuEntities中提取sku的id集合
        List<Long> skuIdList = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());

        // 根据skuId 集合查询对应属性值
        List<SkuAttrValueEntity> attrValueEntities = baseMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().in("sku_id", skuIdList).orderByAsc("attr_id"));
        // [
        //  {attrId: 3, attrName: '颜色', attrValues: '白色','黑色','粉色'},
        //  {attrId: 8, attrName: '内存', attrValues: '6G','8G','12G'},
        //  {attrId: 9, attrName: '存储', attrValues: '128G','256G','512G'}
        // ]
        Map<Long, List<SkuAttrValueEntity>> groupedEntityMap = attrValueEntities.stream().collect(Collectors.groupingBy(attr -> attr.getAttrId()));
        groupedEntityMap.forEach((attrId, attrs) -> {
            SaleAttrValueVo saleAttrValueVo = new SaleAttrValueVo();

            saleAttrValueVo.setAttrId(attrId);
            saleAttrValueVo.setAttrName(attrs.get(0).getAttrName());
            Set<String> valueSet = attrs.stream().map(attr -> attr.getAttrValue()).collect(Collectors.toSet());
            saleAttrValueVo.setAttrValues(valueSet);

            saleAttrValueVos.add(saleAttrValueVo);
        });

        return saleAttrValueVos;
    }

    @Override
    public Map<Long, String> queryCurrentSkuAttrValue(Long skuId) {
        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId));
        return skuAttrValueEntities.stream().collect(Collectors.toMap(item -> item.getAttrId(), item -> item.getAttrValue()));
    }

    @Override
    public String querySkuAttrMapping(Long spuId) {
        // 根据spuId获取SkuEntities
        List<SkuEntity> skuEntities = skuService.list(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        // 从SkuEntities中提取sku的id集合
        List<Long> skuIdList = skuEntities.stream().map(skuEntity -> skuEntity.getId()).collect(Collectors.toList());

        // {'白色,8G,128G': 4, '白色,8G,256G': 5, '白色,8G,512G': 6, '白色,12G,128G': 7}
        List<Map<String, Object>> attrValuesBySkuIds = baseMapper.getAttrValuesBySkuIds(skuIdList);

        Map mappingMap = attrValuesBySkuIds.stream().collect(Collectors.toMap(item -> item.get("attr_values"), item -> item.get("sku_id")));

        return JSON.toJSONString(mappingMap);
    }

}