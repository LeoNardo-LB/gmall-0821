package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.entity.ItemGroupVo;
import com.atguigu.gmall.pms.service.AttrGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性分组
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:23
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    @GetMapping("category/{cid}")
    public ResponseVo<List<AttrGroupEntity>> queryAttrGroupByCid(@PathVariable("cid") Long cid) {
        List<AttrGroupEntity> groupEntity = attrGroupService.queryAttrGroupByCid(cid);
        return ResponseVo.ok(groupEntity);
    }

    @GetMapping("/withattrs/{catId}")
    public ResponseVo<List<AttrGroupEntity>> getAttrsAndGroup(@PathVariable Long catId,
                                                              @RequestParam(value = "type", required = false) Integer type,
                                                              @RequestParam(value = "searchType", required = false) Integer searchType) {
        List<AttrGroupEntity> entityList = attrGroupService.getAttrsAndGroup(catId, type, searchType);
        return ResponseVo.ok(entityList);
    }

    @GetMapping("/sku/spu/{categoryId}")
    public ResponseVo<List<ItemGroupVo>> queryAttrGroupBySkuSpuCategory(@PathVariable Long categoryId,
                                                                        @RequestParam("skuId") Long skuId,
                                                                        @RequestParam("spuId") Long spuId) {
        List<ItemGroupVo> list = attrGroupService.queryAttrGroupBySkuSpuCategory(categoryId, skuId, spuId);
        return ResponseVo.ok(list);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrGroupByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = attrGroupService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrGroupEntity> queryAttrGroupById(@PathVariable("id") Long id) {
        AttrGroupEntity attrGroup = attrGroupService.getById(id);

        return ResponseVo.ok(attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        attrGroupService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
