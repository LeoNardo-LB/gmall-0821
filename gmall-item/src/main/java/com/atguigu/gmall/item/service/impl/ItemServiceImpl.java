package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.item.feign.PmsFeignClient;
import com.atguigu.gmall.item.feign.SmsGmallFeignClient;
import com.atguigu.gmall.item.feign.WmsGmallFeignClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Author: Administrator
 * Create: 2021/2/19
 **/
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    PmsFeignClient pmsFeignClient;

    @Autowired
    SmsGmallFeignClient smsFeignClient;

    @Autowired
    WmsGmallFeignClient wmsFeignClient;

    @Override
    public ItemVo itemDetailsPackaging(Long skuId) {
        ItemVo itemVo = new ItemVo();

        CompletableFuture<SpuEntity> spuEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 根据skuId查询并设置sku信息
            ResponseVo<SkuEntity> skuEntityResponseVo = pmsFeignClient.querySkuById(skuId);
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            if (skuEntity == null) {
                return null;
            }
            itemVo.setSkuId(skuId)
                    .setTitle(skuEntity.getTitle())
                    .setSubTitle(skuEntity.getSubtitle())
                    .setPrice(skuEntity.getPrice())
                    .setWeight(skuEntity.getWeight())
                    .setDefaultImage(skuEntity.getDefaultImage());

            ResponseVo<List<String>> skuImagesVo = pmsFeignClient.querySkuImages(skuId);
            itemVo.setImages(skuImagesVo.getData());
            return skuEntity;
        }).thenApplyAsync(skuEntity -> {
            // 根据skyEntity 的 spuId查询并设置Spu信息
            ResponseVo<SpuEntity> spuEntityResponseVo = pmsFeignClient.querySpuById(skuEntity.getSpuId());
            SpuEntity spuEntity = spuEntityResponseVo.getData();
            if (spuEntity == null) {
                return null;
            }
            Long spuId = spuEntity.getId();
            itemVo.setSpuId(spuId)
                    .setSpuName(spuEntity.getName());
            return spuEntity;
        });

        CompletableFuture<Void> future3 = spuEntityCompletableFuture.thenAcceptAsync(spuEntity -> {
            if (!(spuEntity instanceof SpuEntity)) {
                return;
            }
            // 根据spuEntity的bid查询并设置品牌信息
            ResponseVo<BrandEntity> brandEntityResponseVo = pmsFeignClient.queryBrandById(spuEntity.getBrandId());
            itemVo.setBrandId(spuEntity.getBrandId())
                    .setBrandName(brandEntityResponseVo.getData().getName());
        });

        CompletableFuture<Void> future4 = spuEntityCompletableFuture.thenAcceptAsync(spuEntity -> {
            if (!(spuEntity instanceof SpuEntity)) {
                return;
            }
            // 根据spuEntity的cid查询并设置三级分类信息
            ResponseVo<List<CategoryEntity>> categoriesVo = pmsFeignClient.queryLevel123CategoriesByCid(spuEntity.getCategoryId());
            itemVo.setCategories(categoriesVo.getData());
        });

        CompletableFuture<Void> future5 = spuEntityCompletableFuture.thenAcceptAsync(spuEntity -> {
            if (!(spuEntity instanceof SpuEntity)) {
                return;
            }
            // 根据skuEntity的spuId查询所有销售属性
            ResponseVo<List<SaleAttrValueVo>> skuValuesVo = pmsFeignClient.querySkuValueBySpuId(spuEntity.getId());
            itemVo.setSaleAttrs(skuValuesVo.getData());
        });

        CompletableFuture<Void> future6 = spuEntityCompletableFuture.thenAcceptAsync(spuEntity -> {
            if (!(spuEntity instanceof SpuEntity)) {
                return;
            }
            // 根据skuEntity的spuId查询销售属性组合与映射关系
            ResponseVo<String> attrMappingVo = pmsFeignClient.querySkuAttrMapping(spuEntity.getId());
            itemVo.setSkusJson(attrMappingVo.getData());
        });

        CompletableFuture<Void> future7 = spuEntityCompletableFuture.thenAcceptAsync(spuEntity -> {
            if (!(spuEntity instanceof SpuEntity)) {
                return;
            }
            // 查询spuEntity的spuId spu描述信息(海报)
            ResponseVo<SpuDescEntity> spuDescEntityResponseVo = pmsFeignClient.querySpuDescById(spuEntity.getId());
            String decript = spuDescEntityResponseVo.getData().getDecript();
            itemVo.setSpuImages(Arrays.asList(decript.split(",")));
        });

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // 根据skuId查询并设置营销信息
            ResponseVo<List<ItemSaleVo>> saleStrategyBySkuId = smsFeignClient.getSaleStrategyBySkuId(skuId);
            itemVo.setSales(saleStrategyBySkuId.getData());
        });

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            // 根据skuId查询存货状态并设置
            ResponseVo<List<WareSkuEntity>> wareSkuEntityVo = wmsFeignClient.queryWareSkuBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = wareSkuEntityVo.getData();
            itemVo.setStore(wareSkuEntities.stream().anyMatch(skuWare -> skuWare.getStock() - skuWare.getStockLocked() > 0));
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            // 根据skuId查询当前销售属性
            ResponseVo<Map<Long, String>> currentSkuAttrValueVo = pmsFeignClient.queryCurrentSkuAttrValue(skuId);
            itemVo.setSaleAttr(currentSkuAttrValueVo.getData());
        });

        CompletableFuture<Void> future8 = spuEntityCompletableFuture.thenAcceptAsync((spuEntity) -> {
            // 根据skuId，spuId，cid查询规格参数组及足下规格参数
            ResponseVo<List<ItemGroupVo>> attrGroupVo = pmsFeignClient.queryAttrGroupBySkuSpuCategory(spuEntity.getCategoryId(), skuId, spuEntity.getId());
            itemVo.setGroups(attrGroupVo.getData());
        });

        CompletableFuture.allOf(spuEntityCompletableFuture, future, future1, future2, future3, future4, future5, future6, future7, future8).join();
        return itemVo;
    }

}


class CompletableFutureDemo01 {

    public static void main(String[] args) throws IOException {

        System.out.println("主线程开始...");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("这是future1");
            int i = 10 / 0;
            return "123";
        }).whenCompleteAsync((result, exception) -> {
            System.out.println("whenCompleteAsync - 上个任务的返回值: " + result);
            System.out.println("whenCompleteAsync - 上个任务的异常: " + exception);
        }).exceptionally(ex -> {
            System.out.println("exceptionally - 上个任务的异常: " + ex);
            return "exceptionally 的返回值";
        });

        System.out.println("主线程阻塞中...");

        System.in.read();

        System.out.println("主线程结束!");
    }

}


// 串行
class CompletableFutureDemo02 {

    public static void main(String[] args) throws IOException {

        System.out.println("主线程开始...");

        // 开启一个异步编排线程
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("这是 future 初始方法");
            return "future 1";
        }).thenApply(res -> {   // 串行化
            System.out.println("这是 thenApply 第一个方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 1";
        }).thenApply(res -> {   // 串行化
            System.out.println("这是 thenApply 第二个方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 2";
        });

        System.out.println("主线程阻塞中...");

        System.in.read();

        System.out.println("主线程结束!");
    }

}


// 串行
class CompletableFutureDemo03 {

    public static void main(String[] args) throws IOException {

        System.out.println("主线程开始...");

        // 开启一个异步编排线程
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("这是 future 初始方法");
            return "future 1";
        }).thenApply(res -> {   // 串行化
            System.out.println("这是 thenApply 第一个方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 1";
        }).thenApply(res -> {   // 串行化
            System.out.println("这是 thenApply 第二个方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 2";
        });

        future.thenApply(res -> {   // 并行添加多个 串并行方法
            System.out.println("这是 thenApply 第一个并行方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 1";
        });
        future.thenApply(res -> {   // 并行添加多个 串并行方法
            System.out.println("这是 thenApply 第二个并行方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 2";
        });
        future.thenApply(res -> {   // 并行添加多个 串并行方法
            System.out.println("这是 thenApply 第三个并行方法");
            System.out.println("上个方法返回值为: " + res);
            return "thenApply 3";
        });
        System.out.println("主线程阻塞中...");

        System.in.read();

        System.out.println("主线程结束!");
    }

}


// 组合使用
class CompletableFutureDemo04 {

    public static void main(String[] args) throws IOException {

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("开启第一个任务, 耗时1s");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "mission 1";
        });

        future1.thenApplyAsync(s -> {
            System.out.println("开启第一个任务的串行任务1, 耗时2s, 上个任务的返回值为: " + s);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "mission 3";
        }).thenApplyAsync(s -> {
            System.out.println("开启第一个任务的串行任务2, 耗时3s, 上个任务的返回值为: " + s);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "mission 4";
        }).thenApplyAsync(s -> {
            System.out.println("开启第一个任务的串行任务3, 耗时4s, 上个任务的返回值为: " + s);
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "mission 5";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("开启第一个任务的并行任务, 耗时12s");
            try {
                TimeUnit.SECONDS.sleep(12);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "mission 2";
        });

        CompletableFuture.allOf(future1, future2).thenAcceptAsync(res -> {
            System.out.println("以上所有任务都完成了, 返回值为: " + res);
        }).join();  // join 可以在执行完该线程后再执行当前线程(主线程)
    }

}


