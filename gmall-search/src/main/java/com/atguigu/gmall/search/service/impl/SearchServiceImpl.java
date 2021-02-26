package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.entity.*;
import com.atguigu.gmall.search.feign.PmsFeignClient;
import com.atguigu.gmall.search.feign.WmsGmallFeignClient;
import com.atguigu.gmall.search.mapper.GoodsRepository;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.jsonwebtoken.lang.Collections;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Administrator
 * Create: 2021/2/1
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    PmsFeignClient pmsFeignClient;

    @Autowired
    WmsGmallFeignClient wmsFeignClient;

    @Override
    public SearchResponseVo search(SearchParamVo searchParam) {

        try {
            SearchRequest searchRequest = new SearchRequest(new String[]{"goods"}, buildDsl(searchParam));
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchResponseVo responseVo = parseResult(searchResponse);
            responseVo.setPageNum(searchParam.getPageNum());
            responseVo.setPageSize(searchParam.getPageSize());
            return responseVo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SearchResponseVo parseResult(SearchResponse searchResponse) {
        // 从searchResponse对象中提取信息到 SearchResponseVo中
        SearchResponseVo responseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        // 设置总记录条数
        responseVo.setTotal(hits.getTotalHits());

        // 1. 解析商品结果集
        SearchHit[] hitsHits = hits.getHits();
        List<Goods> goodList = Arrays.stream(hitsHits).map(hit -> {
            String sourceJson = hit.getSourceAsString();
            Goods goods = JSON.parseObject(sourceJson, Goods.class);

            // 使用高亮片段覆盖掉标题
            goods.setTitle(hit.getHighlightFields().get("title").getFragments()[0].toString());
            return goods;
        }).collect(Collectors.toList());
        responseVo.setGoodsList(goodList);

        // 2.聚合结果集的解析
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        // 2.1 解析分类结果集
        ParsedLongTerms categorgIdAgg = (ParsedLongTerms) aggregationMap.get("categoryIdAgg");
        List<? extends Terms.Bucket> categorgIdAggBuckets = categorgIdAgg.getBuckets();
        ArrayList<CategoryEntity> categoryEntities = new ArrayList<>();
        if (!Collections.isEmpty(categorgIdAggBuckets)) {
            List<CategoryEntity> categorgNameAgg = categorgIdAggBuckets.stream().map(item -> {
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setId(item.getKeyAsNumber().longValue());
                ParsedStringTerms categoryName = (ParsedStringTerms) item.getAggregations().asMap().get("categoryNameAgg");
                categoryEntity.setName(categoryName.getBuckets().get(0).getKey().toString());
                return categoryEntity;
            }).collect(Collectors.toList());
            responseVo.setCategories(categoryEntities);
        }
        // 2.2 解析品牌结果集
        ParsedLongTerms brandIdAgg = (ParsedLongTerms) aggregationMap.get("brandIdAgg");
        List<? extends Terms.Bucket> brandIdAggBuckets = brandIdAgg.getBuckets();
        if (!Collections.isEmpty(brandIdAggBuckets)) {
            List<BrandEntity> brandList = brandIdAggBuckets.stream().map(item -> {
                BrandEntity brandEntity = new BrandEntity();
                // 设置品牌id
                brandEntity.setId(item.getKeyAsNumber().longValue());
                // 设置品牌Logo
                ParsedStringTerms brandLogoAgg = (ParsedStringTerms) item.getAggregations().get("brandLogoAgg");
                brandEntity.setLogo(brandLogoAgg.getBuckets().get(0).getKey().toString());
                // 设置品牌name
                ParsedStringTerms brandNameAgg = (ParsedStringTerms) item.getAggregations().get("brandNameAgg");
                brandEntity.setName(brandNameAgg.getBuckets().get(0).getKey().toString());
                return brandEntity;
            }).collect(Collectors.toList());
            responseVo.setBrands(brandList);
        }
        // 2.3 解析聚合结果集
        ParsedNested attrAgg = (ParsedNested) aggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();
        if (!Collections.isEmpty(attrIdAggBuckets)) {
            List<SearchResponseAttrVo> searchResponseAttrVoList = attrIdAggBuckets.stream().map(item -> {
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                // 设置嵌套类型id
                searchResponseAttrVo.setAttrId(item.getKeyAsNumber().longValue());
                // 设置嵌套类型名称
                ParsedStringTerms attrNameAgg = (ParsedStringTerms) item.getAggregations().get("attrNameAgg");
                searchResponseAttrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
                // 设置嵌套类型值
                ParsedStringTerms attrValueAgg = (ParsedStringTerms) item.getAggregations().get("attrValueAgg");
                List<? extends Terms.Bucket> attrValueAggBuckets = attrValueAgg.getBuckets();
                List<String> attrValueList = attrValueAggBuckets.stream().map(valueItem -> {
                    return valueItem.getKeyAsString();
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValues(attrValueList);
                return searchResponseAttrVo;
            }).collect(Collectors.toList());
            responseVo.setFilters(searchResponseAttrVoList);
        }

        return responseVo;
    }

    /**
     * 构建DSL语句
     * @param searchParam
     * @return
     */
    public SearchSourceBuilder buildDsl(SearchParamVo searchParam) {
        // 通过 SearchSourceBuilder 对象来构建DSL语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 1. 构建 bool 查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1.1 添加must项：设置查询的title为传过来的字段，设置连接符为AND
        boolQueryBuilder.must().add(QueryBuilders.matchQuery("title", searchParam.getKeyword()).operator(Operator.AND));
        // 1.2 构建过滤
        // 1.2.1
        if (!Collections.isEmpty(searchParam.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        // 1.2.2 分类过滤
        if (!Collections.isEmpty(searchParam.getCategoryId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId", searchParam.getCategoryId()));
        }
        // 1.2.3 价格区间过滤
        if (searchParam.getPriceFrom() != null && searchParam.getPriceTo() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(searchParam.getPriceFrom()).lte(searchParam.getPriceTo()));
        }
        // 1.2.4 规格参数过滤
        if (!Collections.isEmpty(searchParam.getProps())) {
            searchParam.getProps().forEach(prop -> {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                // 解析prop：props=5:高通-麒麟,6:骁龙865-硅谷1000
                String[] parsedProp = prop.split(":");
                if (parsedProp != null) {
                    String attrId = parsedProp[0];
                    String[] attrValueList = parsedProp[1].split("-");
                    boolQuery.must(QueryBuilders.termQuery("searchAttrs.attrId", attrId));
                    boolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrValue", attrValueList));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs", boolQuery, ScoreMode.None));
                    // boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs", QueryBuilders.termsQuery("searchAttrs.attrId", attrId), ScoreMode.None));
                    // boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs", QueryBuilders.termsQuery("searchAttrs.attrValue", attrValueList), ScoreMode.None));
                }
            });
        }
        sourceBuilder.query(boolQueryBuilder);

        // 2. 构建排序 0-默认，得分降序；1-按价格升序；2-按价格降序；3-按创建时间降序；4-按销量降序
        SortOrder order = null;
        String field = "";
        switch (searchParam.getSort()) {
            case 1:
                field = "price";
                order = SortOrder.ASC;
                break;
            case 2:
                field = "price";
                order = SortOrder.DESC;
                break;
            case 3:
                field = "createTime";
                order = SortOrder.DESC;
                break;
            case 4:
                field = "sales";
                order = SortOrder.DESC;
                break;
            default:
                field = "_score";
                order = SortOrder.DESC;
        }
        sourceBuilder.sort(field, order);

        // 3. 构建分页
        Integer pageNum = searchParam.getPageNum();
        Integer pageSize = searchParam.getPageSize();
        sourceBuilder.from(pageSize * (pageNum - 1));
        sourceBuilder.size(pageSize);

        // 4. 构建高亮字段
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<font style='color:red'>").postTags("</font>"));

        // 5. 构建聚合结果集
        // 5.1 构建品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                                          .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                                          .subAggregation(AggregationBuilders.terms("brandLogoAgg").field("logo")));
        // 5.2 构建分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                                          .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));
        // 5.3 构建属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "searchAttrs")
                                          .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                                                                  .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName"))
                                                                  .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue"))));
        // 6. 构建结果集过滤
        sourceBuilder.fetchSource(new String[]{"skuId", "title", "price", "defaultImage"}, null);
        System.out.println(sourceBuilder.toString());
        return sourceBuilder;
    }

    public void createIndex(Long spuId) {
        ResponseVo<SpuEntity> spuEntityResponseVo = pmsFeignClient.querySpuById(spuId);
        SpuEntity spuItem = spuEntityResponseVo.getData();
        ResponseVo<List<SkuEntity>> skuRespVo = pmsFeignClient.getSkuListById(spuId);
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
                goodsRepository.save(goods);
            });
        }
    }

}
