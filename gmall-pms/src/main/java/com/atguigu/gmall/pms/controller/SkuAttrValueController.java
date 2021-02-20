package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SaleAttrValueVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:22
 */
@Api(tags = "sku销售属性&值 管理")
@RestController
@RequestMapping("pms/skuattrvalue")
public class SkuAttrValueController {

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    /**
     * 通过spu获取所有属性列表
     * @param spuId
     * @return
     */
    @GetMapping("/sku/value/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySkuValueBySpuId(@PathVariable Long spuId) {
        List<SaleAttrValueVo> list = skuAttrValueService.querySkuValueBySpuId(spuId);
        return ResponseVo.ok(list);
    }

    /**
     * 通过sku获取当前属性列表
     * @param skuId
     * @return
     */
    @GetMapping("/sku/value/current/{skuId}")
    public ResponseVo<Map<Long, String>> queryCurrentSkuAttrValue(@PathVariable Long skuId) {
        Map<Long, String> map = skuAttrValueService.queryCurrentSkuAttrValue(skuId);
        return ResponseVo.ok(map);
    }

    @GetMapping("/spu/sku/{spuId}")
    public ResponseVo<String> querySkuAttrMapping(@PathVariable Long spuId){
        String json = skuAttrValueService.querySkuAttrMapping(spuId);
        return ResponseVo.ok(json);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySkuAttrValueByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = skuAttrValueService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuAttrValueEntity> querySkuAttrValueById(@PathVariable("id") Long id) {
        SkuAttrValueEntity skuAttrValue = skuAttrValueService.getById(id);

        return ResponseVo.ok(skuAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuAttrValueEntity skuAttrValue) {
        skuAttrValueService.save(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuAttrValueEntity skuAttrValue) {
        skuAttrValueService.updateById(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        skuAttrValueService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
