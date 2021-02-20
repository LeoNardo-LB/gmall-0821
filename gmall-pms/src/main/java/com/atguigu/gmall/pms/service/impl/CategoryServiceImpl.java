package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> queryCategory(Long pid) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        if (pid != -1) {
            wrapper.eq("parent_id", pid);
        }
        List<CategoryEntity> categoryEntities = baseMapper.selectList(wrapper);
        return categoryEntities;
    }

    @Override
    public List<CategoryEntity> queryLevel23Categories(Long pid) {
        return categoryMapper.queryLevel23Categories(pid);
    }

    @Override
    public List<CategoryEntity> queryLevel123CategoriesByCid(Long cid) {
        // 查询一级分类
        CategoryEntity categoryEntityLevel1 = categoryMapper.selectById(cid);

        // 查询二级分类
        CategoryEntity categoryEntityLevel2 = categoryMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id", categoryEntityLevel1.getParentId()));

        // 查询三级分类
        CategoryEntity categoryEntityLevel3 = categoryMapper.selectOne(new QueryWrapper<CategoryEntity>().eq("id", categoryEntityLevel2.getParentId()));

        ArrayList<CategoryEntity> categoryEntities = new ArrayList<>(3);

        categoryEntities.add(categoryEntityLevel1);
        categoryEntities.add(categoryEntityLevel2);
        categoryEntities.add(categoryEntityLevel3);

        return categoryEntities;
    }

}