package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品属性
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:23
 */
@Api(tags = "商品属性 管理")
@RestController
@RequestMapping("pms/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @GetMapping("/sku/searchType/{sid}")
    public ResponseVo<List<SkuAttrValueEntity>> getSkuSearchTypeBySkuId(@PathVariable Long sid) {
        List<SkuAttrValueEntity> list = attrService.getSkuSearchTypeBySkuId(sid);
        return ResponseVo.ok(list);
    }

    @GetMapping("/spu/searchType/{sid}")
    public ResponseVo<List<SpuAttrValueEntity>> getSpuSearchTypeBySpuId(@PathVariable Long sid) {
        List<SpuAttrValueEntity> list = attrService.getSpuSearchTypeBySpuId(sid);
        return ResponseVo.ok(list);
    }

    @GetMapping("/category/{cid}")
    public ResponseVo<List<AttrEntity>> getAttrOfCategory(@PathVariable("cid") Long cid,
                                                          @RequestParam(value = "type", required = false) Integer type,
                                                          @RequestParam(value = "searchType", required = false) Integer searchType) {
        List<AttrEntity> attrEntities = attrService.getAttrOfCategory(cid, type, searchType);
        return ResponseVo.ok(attrEntities);
    }

    @GetMapping("/group/{gid}")
    public ResponseVo<List<AttrEntity>> getAttrByGid(@PathVariable Long gid) {
        List<AttrEntity> entityList = attrService.list(new QueryWrapper<AttrEntity>().eq("group_id", gid));
        return ResponseVo.ok(entityList);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = attrService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrEntity> queryAttrById(@PathVariable("id") Long id) {
        AttrEntity attr = attrService.getById(id);

        return ResponseVo.ok(attr);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrEntity attr) {
        attrService.save(attr);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrEntity attr) {
        attrService.updateById(attr);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        attrService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
