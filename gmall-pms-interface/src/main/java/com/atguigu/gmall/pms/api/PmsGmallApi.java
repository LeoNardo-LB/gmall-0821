package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/1/31
 **/
public interface PmsGmallApi {

    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    @PostMapping("pms/spu/json")
    public ResponseVo<List<SpuEntity>> querySpuByPageJson(PageParamVo paramVo);

    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> getSkuListById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    @GetMapping("pms/category/parent/{pid}")
    public ResponseVo<List<CategoryEntity>> queryCategory(@PathVariable("pid") Long pid);

    /**
     * 根据sku中的三级分类id查询一二三级分类
     * @param cid
     * @return
     */
    @GetMapping("pms/category/level123/{cid}")
    public ResponseVo<List<CategoryEntity>> queryLevel123CategoriesByCid(@PathVariable Long cid);

    /**
     * 根据skuId查询sku所有图片
     * @param skuId
     * @return
     */
    @GetMapping("pms/sku/images/{skuId}")
    public ResponseVo<List<String>> querySkuImages(@PathVariable Long skuId);

    /**
     * 根据sku中的spuId查询spu下的所有销售属性
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuattrvalue/sku/value/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySkuValueBySpuId(@PathVariable Long spuId);

    /**
     * 根据skuId查询当前sku的销售属性
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuattrvalue/sku/value/current/{skuId}")
    public ResponseVo<Map<Long, String>> queryCurrentSkuAttrValue(@PathVariable Long skuId);

    /**
     * 根据sku中的spuId查询spu下所有sku：销售属性组合与skuId映射关系
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuattrvalue/spu/sku/{spuId}")
    public ResponseVo<String> querySkuAttrMapping(@PathVariable Long spuId);

    /**
     * 根据分类id、spuId及skuId查询分组及组下的规格参数值
     * @param categoryId
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/sku/spu/{categoryId}")
    public ResponseVo<List<ItemGroupVo>> queryAttrGroupBySkuSpuCategory(@PathVariable Long categoryId,
                                                                        @RequestParam("skuId") Long skuId,
                                                                        @RequestParam("spuId") Long spuId);

    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    @GetMapping("pms/attr/spu/searchType/{sid}")
    public ResponseVo<List<SpuAttrValueEntity>> getSpuSearchTypeBySpuId(@PathVariable Long sid);

    @GetMapping("pms/attr/sku/searchType/{sid}")
    public ResponseVo<List<SkuAttrValueEntity>> getSkuSearchTypeBySkuId(@PathVariable Long sid);

    @GetMapping("pms/category/level23/{pid}")
    List<CategoryEntity> queryLevel23Categories(@PathVariable Long pid);

}
