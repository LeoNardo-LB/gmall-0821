package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrGroupMapper attrGroupMapper;

    @Autowired
    AttrService attrService;

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

}