package com.atguigu.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.entity.Goods;
import com.atguigu.gmall.search.entity.SearchAttrValue;
import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsFeignClient;
import com.atguigu.gmall.search.mapper.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.jsonwebtoken.lang.Collections;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchRestTemplate restTemplate;

    @Autowired
    PmsFeignClient pmsFeignClient;

    @Autowired
    WmsFeignClient wmsFeignClient;

    @Test
    public void importData() {
        ArrayList<Goods> goodList = new ArrayList<>();
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        Integer pageNum = 1;
        Integer pageSize = 20;
        do {
            System.out.println("分页："+pageNum);
            // - 分页查询已上架的SPU信息
            PageParamVo pageParamVo = new PageParamVo(pageNum, pageSize, null);
            ResponseVo<List<SpuEntity>> spuResponseVo = pmsFeignClient.querySpuByPageJson(pageParamVo);

            // 获取到 SpuEntity 集合
            List<SpuEntity> spuList = spuResponseVo.getData();
            if (Collections.isEmpty(spuList)) {
                break;
            }

            // - 根据SpuId查询对应的SKU信息（接口已写好）
            spuList.forEach(spuItem -> {
                ResponseVo<List<SkuEntity>> skuRespVo = pmsFeignClient.getSkuListById(spuItem.getId());
                List<SkuEntity> skuList = skuRespVo.getData();
                if (!Collections.isEmpty(skuList)) {
                    skuList.forEach(skuItem -> {
                        Goods goods = new Goods();
                        // 设置sku的基本信息
                        goods.setSkuId(skuItem.getId());
                        goods.setTitle(skuItem.getTitle());
                        goods.setSubTitle(skuItem.getSubtitle());
                        goods.setDefaultImage(skuItem.getDefaultImage());
                        goods.setPrice(skuItem.getPrice().doubleValue());
                        goods.setCreateTime(spuItem.getCreateTime());
                        goods.setSales(0l);

                        // - 根据分类id查询商品分类（逆向工程已自动生成）
                        ResponseVo<CategoryEntity> queryCategoryById = pmsFeignClient.queryCategoryById(skuItem.getCategoryId());
                        CategoryEntity categoryEntity = queryCategoryById.getData();
                        if (categoryEntity != null) {
                            goods.setCategoryId(skuItem.getCategoryId());
                            goods.setCategoryName(categoryEntity.getName());
                        }

                        // - 根据品牌id查询品牌（逆向工程已自动生成）
                        ResponseVo<BrandEntity> queryBrandById = pmsFeignClient.queryBrandById(skuItem.getBrandId());
                        BrandEntity brandEntity = queryBrandById.getData();
                        if (brandEntity != null) {
                            goods.setBrandId(skuItem.getBrandId());
                            goods.setBrandName(brandEntity.getName());
                            goods.setLogo(brandEntity.getLogo());
                        }

                        // - 根据skuid查询库存（gmall-wms中接口已写好）
                        ResponseVo<List<WareSkuEntity>> queryWareSkuId = wmsFeignClient.queryWareSkuBySkuId(skuItem.getId());
                        List<WareSkuEntity> wareData = queryWareSkuId.getData();
                        if (!Collections.isEmpty(wareData)) {
                            goods.setStore(wareData.stream().anyMatch(ware -> {
                                return (ware.getStock() - ware.getStockLocked() > 0);
                            }));
                        }

                        ArrayList<SearchAttrValue> searchAttrValues = new ArrayList<>();
                        // - 根据spuId查询检索规格参数及值
                        ResponseVo<List<SpuAttrValueEntity>> spuAttrRespVo = pmsFeignClient.getSpuSearchTypeBySpuId(spuItem.getId());
                        List<SpuAttrValueEntity> spuAttrList = spuAttrRespVo.getData();
                        if (!Collections.isEmpty(spuAttrList)) {
                            spuAttrList.stream().forEach(spuAttrEntity -> {
                                searchAttrValues.add(new SearchAttrValue()
                                                             .setAttrId(spuAttrEntity.getAttrId())
                                                             .setAttrName(spuAttrEntity.getAttrName())
                                                             .setAttrValue(spuAttrEntity.getAttrValue()));
                            });
                        }
                        // - 根据skuId查询检索规格参数及值
                        ResponseVo<List<SkuAttrValueEntity>> skuAttrRespVo = pmsFeignClient.getSkuSearchTypeBySkuId(skuItem.getId());
                        List<SkuAttrValueEntity> skuAttrList = skuAttrRespVo.getData();
                        if (!Collections.isEmpty(skuAttrList)) {
                            skuAttrList.stream().forEach(skuAttrEntity -> {
                                searchAttrValues.add(new SearchAttrValue()
                                                             .setAttrId(skuAttrEntity.getAttrId())
                                                             .setAttrName(skuAttrEntity.getAttrName())
                                                             .setAttrValue(skuAttrEntity.getAttrValue()));
                            });
                        }
                        goods.setSearchAttrs(searchAttrValues);
                        goodList.add(goods);
                    });
                    goodsRepository.saveAll(goodList);
                }
            });
            // 使分页进1
            pageSize = spuList.size();
            pageNum++;
        } while (pageSize == 20);
    }

}
