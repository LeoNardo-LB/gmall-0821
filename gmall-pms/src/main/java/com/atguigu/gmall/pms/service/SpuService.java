package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SpuEntity;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:22
 */
public interface SpuService extends IService<SpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    PageResultVo getSpuEntityByCondition(Long categoryId, PageParamVo paramVo);

    void bigSave(SpuSaveVo spuSaveVo);

}

