package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.service.AttrService;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrGroupMapper attrGroupMapper;

    @Autowired
    AttrService attrService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SpuAttrValueService spuAttrValueService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<AttrGroupEntity> queryAttrGroupByCid(Long cid) {
        List<AttrGroupEntity> list = attrGroupMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));
        return list;
    }

    // 使用分布查询查到attrGroup 与 对应的attr
    @Override
    public List<AttrGroupEntity> getAttrsAndGroup(Long catId, Integer type, Integer searchType) {
        List<AttrGroupEntity> groupEntities = attrGroupMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", catId));
        groupEntities.forEach(a -> {
            QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("type", 1).eq("group_id", a.getId());
            a.setAttrEntities(attrService.list(wrapper));
        });
        return groupEntities;
    }

    @Override
    public List<ItemGroupVo> queryAttrGroupBySkuSpuCategory(Long categoryId, Long skuId, Long spuId) {
        List<AttrGroupEntity> groupEntities = attrGroupMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("category_id", categoryId));
        if (CollectionUtils.isEmpty(groupEntities)) {
            return null;
        }

        List<ItemGroupVo> itemGroupVos = groupEntities.stream().map(group -> {
            ItemGroupVo itemGroupVo = new ItemGroupVo();

            itemGroupVo.setGroupId(group.getId());
            itemGroupVo.setGroupName(group.getName());

            // 通过group_id 找到每个attr的实体类，通过attr的实体类id，与传入的spu、sku查询具体的attrValue
            List<AttrEntity> attrEntities = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", group.getId()));
            List<Long> attrIdList = attrEntities.stream().map(entity -> entity.getId()).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(attrIdList)) {
                // 查询 sku的attrValue
                List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.list(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).in("attr_id", attrIdList));

                // 查询 spu的attrValue
                List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrValueService.list(new QueryWrapper<SpuAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIdList));

                // 包装到 list 中
                List<AttrValueVo> attrValueVoList = new ArrayList<>();
                itemGroupVo.setAttrValues(attrValueVoList);

                if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                    skuAttrValueEntities.forEach(entity -> {
                        AttrValueVo attrValueVo = new AttrValueVo();
                        BeanUtils.copyProperties(entity, attrValueVo);
                        attrValueVoList.add(attrValueVo);
                    });
                }
                if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                    spuAttrValueEntities.forEach(entity -> {
                        AttrValueVo attrValueVo = new AttrValueVo();
                        BeanUtils.copyProperties(entity, attrValueVo);
                        attrValueVoList.add(attrValueVo);
                    });
                }
            }

            return itemGroupVo;
        }).collect(Collectors.toList());

        return itemGroupVos;
    }

}