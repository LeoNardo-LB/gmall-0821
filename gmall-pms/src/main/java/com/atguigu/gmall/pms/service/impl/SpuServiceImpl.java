package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.controller.Dto.SmsSaveDto;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.Vo.SkuSaveVo;
import com.atguigu.gmall.pms.Vo.SpuSaveVo;
import com.atguigu.gmall.pms.feign.GmallSmsFeignClient;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.lang.Collections;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("spuService")

public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    SpuMapper spuMapper;

    @Autowired
    SpuDescService spuDescService;

    @Autowired
    SpuAttrValueService spuAttrValueService;

    @Autowired
    SkuService skuService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    GmallSmsFeignClient gmallSmsFeignClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo getSpuEntityByCondition(Long categoryId, PageParamVo paramVo) {
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
        if (categoryId != 0) {
            wrapper.eq("category_id", categoryId);
        }

        if (!StringUtils.isEmpty(paramVo.getKey())) {
            wrapper.and(e -> e.like("name", paramVo.getKey()).or().eq("id", paramVo.getKey()));
        }

        IPage<SpuEntity> page = spuMapper.selectPage(paramVo.getPage(), wrapper);
        return new PageResultVo(page);
    }

    @GlobalTransactional
    @Override
    public void bigSave(SpuSaveVo spuSaveVo) {
        SpuEntity spuEntity = new SpuEntity();
        // 1.保存spu信息
        // 1.1 保存pms_spu表的信息
        BeanUtils.copyProperties(spuSaveVo, spuEntity);
        spuEntity.setCreateTime(new Date());
        spuEntity.setUpdateTime(spuEntity.getCreateTime());
        this.save(spuEntity);

        Long spuId = spuEntity.getId();
        Long categoryId = spuSaveVo.getCategoryId();
        Long brandId = spuSaveVo.getBrandId();
        // 1.2 保存 pms_spu_desc表的信息
        ArrayList<SpuDescEntity> spuDescEntities = new ArrayList<>();
        spuSaveVo.getSpuImages().forEach(img -> {
            spuDescEntities.add(new SpuDescEntity().setSpuId(spuId).setDecript(img));
        });
        spuDescService.saveBatch(spuDescEntities);

        // 1.3 保存基本属性
        ArrayList<SpuAttrValueEntity> spuAttrValueEntities = new ArrayList<>();
        spuSaveVo.getBaseAttrs().forEach(spuAttrVo -> {
            SpuAttrValueEntity attrValueEntity = new SpuAttrValueEntity();
            BeanUtils.copyProperties(spuAttrVo, attrValueEntity);
            attrValueEntity.setAttrValue(spuAttrVo.getValueSelected()).setSpuId(spuId);
            spuAttrValueEntities.add(attrValueEntity);
        });
        spuAttrValueService.saveBatch(spuAttrValueEntities);

        // 2.保存Sku信息
        List<SkuSaveVo> skus = spuSaveVo.getSkus();
        skus.forEach(item -> {
            // 2.1 保存到 pms_sku
            SkuEntity skuEntity = new SkuEntity();
            BeanUtils.copyProperties(item, skuEntity);
            skuEntity.setSpuId(spuId).setCategoryId(categoryId).setBrandId(brandId)
                    .setDefaultImage(Collections.isEmpty(item.getImages()) ? null : item.getImages().get(0));
            skuService.save(skuEntity);

            Long skuId = skuEntity.getId();

            // 2.2 保存 pms_sku_attr_value
            List<SkuAttrValueEntity> saleAttrs = item.getSaleAttrs();
            saleAttrs.forEach(saleAttr -> {
                saleAttr.setSkuId(skuId);
            });
            skuAttrValueService.saveBatch(saleAttrs);

            // 2.3 保存到pms_sku_images
            List<String> images = item.getImages();
            if (Collections.isEmpty(images)) {
                return;
            }
            ArrayList<SkuImagesEntity> skuImagesEntities = new ArrayList<>();
            images.forEach(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId).setUrl(img);
                skuImagesEntities.add(skuImagesEntity);
            });
            skuImagesService.saveBatch(skuImagesEntities);

            // 3 调用sms接口保存sms信息
            SmsSaveDto smsSaveDto = new SmsSaveDto();
            BeanUtils.copyProperties(item, smsSaveDto);
            smsSaveDto.setSkuId(skuId);
            gmallSmsFeignClient.saveSkuBounds(smsSaveDto);

            // 制造错误，如果全局事务有效，则都不保存。
            // int i = 10 / 0;
        });

        // 4. 通过RabbitMQ 异步保存到ElasticSearch中
        try {
            rabbitTemplate.convertAndSend("ITEM_EXCHANGE", "item.*", spuId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}