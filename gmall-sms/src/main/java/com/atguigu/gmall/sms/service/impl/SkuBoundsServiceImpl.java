package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.controller.Dto.SmsSaveDto;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuFullReductionService;
import com.atguigu.gmall.sms.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;

@Service("skuBoundsService")
@Transactional
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    SkuBoundsService skuBoundsService;

    @Autowired
    SkuFullReductionService skuFullReductionService;

    @Autowired
    SkuLadderService skuLadderService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public void saveSaleInfo(SmsSaveDto smsSaveDto) {
        // 设置积分
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(smsSaveDto, skuBoundsEntity);
        List<Integer> work = smsSaveDto.getWork();
        skuBoundsEntity.setWork(work.get(0) * 8 + work.get(1) * 4 + work.get(2) * 2 + work.get(3) * 1);

        // 设置满减活动
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(smsSaveDto, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(smsSaveDto.getFullAddOther());

        // 设置折扣活动
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(smsSaveDto, skuLadderEntity);
        skuLadderEntity.setAddOther(smsSaveDto.getLadderAddOther());

        // 保存三个销售活动对象
        skuBoundsService.save(skuBoundsEntity);
        skuFullReductionService.save(skuFullReductionEntity);
        skuLadderService.save(skuLadderEntity);
    }

}