package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.entity.UserInfo;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.service.AsyncCarService;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.controller.Dto.ItemSaleVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Administrator
 * Create: 2021/2/24
 **/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    GmallPmsClient gmallPmsClient;

    @Autowired
    GmallSmsClient gmallSmsClient;

    @Autowired
    GmallWmsClient gmallWmsClient;

    @Autowired
    AsyncCarService asyncCarService;

    private static final String REDIS_CART_PREFIX = "cart:info:";

    private static final String REDIS_SKU_PRICE_PREFIX = "cart:price:";

    @Override
    public void addCart(Cart cart) {
        if (cart == null || cart.getCount() == null || cart.getCount().intValue() == 0) {
            throw new RuntimeException("购物车有误！");
        }

        // 1. 获取UserInfo
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userId = userInfo.getUserId();

        // 2. 判断登陆状态，未登录用userKey，登录用userInfo
        String key = userId;
        if (StringUtils.isBlank(userId)) {
            key = userInfo.getUserKey();
        }
        String redisKey = REDIS_CART_PREFIX + key;
        cart.setUserId(key);

        // 3. 获取购物车信息
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(redisKey);
        if (hashOps.hasKey(cart.getSkuId().toString())) {
            BigDecimal count = cart.getCount();
            // 如果有，则新增count个sku数量，再保存
            cart = JSON.parseObject(hashOps.get(cart.getSkuId().toString()).toString(), Cart.class);
            cart.setCount(cart.getCount().add(count));
            String toSaveRedisJson = JSON.toJSONString(cart);
            // hashOps.put(cart.getSkuId(), toSaveRedisJson);
            // 【异步】保存到数据库
            asyncCarService.modify(key, cart);
        } else {
            // 如果没有，则创建新的记录并新增count个数量
            ResponseVo<SkuEntity> skuEntityResponseVo = gmallPmsClient.querySkuById(cart.getSkuId());
            // 查询skuEntity
            SkuEntity skuEntity = skuEntityResponseVo.getData();
            if (skuEntity != null) {
                cart.setDefaultImage(skuEntity.getDefaultImage())
                        .setPrice(skuEntity.getPrice())
                        .setTitle(skuEntity.getTitle());
            }
            // 查询saleAttrs
            ResponseVo<List<SkuAttrValueEntity>> attrValuesResponseVo = gmallPmsClient.queryAttrValueEntitysBySkuId(cart.getSkuId());
            List<SkuAttrValueEntity> attrValues = attrValuesResponseVo.getData();
            if (!CollectionUtils.isEmpty(attrValues)) {
                String attrValuesJson = JSON.toJSONString(attrValues);
                cart.setSaleAttrs(attrValuesJson);
            }
            // 查询库存
            ResponseVo<List<WareSkuEntity>> wareSkuResponseVo = gmallWmsClient.queryWareSkuBySkuId(cart.getSkuId());
            List<WareSkuEntity> wareSkuEntityList = wareSkuResponseVo.getData();
            if (CollectionUtils.isEmpty(wareSkuEntityList)) {
                cart.setStore(wareSkuEntityList.stream().allMatch(ware -> ware.getStock() - ware.getStockLocked() > 0));
            }
            // 查询营销策略
            ResponseVo<List<ItemSaleVo>> saleStrategyResponseVo = gmallSmsClient.getSaleStrategyBySkuId(cart.getSkuId());
            List<ItemSaleVo> sales = saleStrategyResponseVo.getData();
            if (!CollectionUtils.isEmpty(sales)) {
                cart.setSales(JSON.toJSONString(sales));
            }
            // 【异步】保存到Mysql
            asyncCarService.add(cart);
        }
        // 保存购物车到Redis
        hashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));

        // 保存实时价格到Redis
        stringRedisTemplate.opsForValue().set(REDIS_SKU_PRICE_PREFIX + cart.getSkuId(), cart.getPrice().toString());
    }

    @Override
    public Cart queryCartBySkuId(Long skuId) {
        // 1. 获取UserInfo
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userId = userInfo.getUserId();

        // 2. 判断登陆状态，未登录用userKey，登录用userInfo
        String key = userId;
        if (StringUtils.isBlank(userId)) {
            key = userInfo.getUserKey();
        }
        String redisKey = REDIS_CART_PREFIX + key;
        String cartJson = stringRedisTemplate.opsForHash().get(redisKey, skuId.toString()).toString();
        return JSON.parseObject(cartJson, Cart.class);
    }

    @Override
    public List<Cart> queryCarts() {

        // 1.获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = userInfo.getUserId();
        if (StringUtils.isBlank(key)) { // 判断key类型
            // 如果未登录，则直接查询Redis中的userKey对应的记录返回
            key = REDIS_CART_PREFIX + userInfo.getUserKey();
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
            if (CollectionUtils.isEmpty(entries)) {
                return new ArrayList<Cart>();
            }
            return entries.entrySet().stream().map(entry -> {
                return JSON.parseObject(entry.getValue().toString(), Cart.class);
            }).collect(Collectors.toList());
        }

        // -----------下面的逻辑是已登录的情况-----------

        // 2. 合并购物车
        // 2.1 分别查询出Redis中登录与未登录的购物车数据
        String unloginRedisKey = REDIS_CART_PREFIX + userInfo.getUserKey();
        String loginRedisKey = REDIS_CART_PREFIX + key;
        Map<Object, Object> unloginEntires = stringRedisTemplate.opsForHash().entries(unloginRedisKey);
        Map<Object, Object> loginEntires = stringRedisTemplate.opsForHash().entries(loginRedisKey);
        final String _key = key;
        unloginEntires.entrySet().stream().forEach(entry -> {
            if (loginEntires.containsKey(entry.getKey().toString())) {
                // 如果 已登录的购物车中含有未登录的购物车的sku，则数量累加
                Cart loginCartRecord = JSON.parseObject(loginEntires.get(entry.getKey().toString()).toString(), Cart.class);
                Cart unloginCartRecord = JSON.parseObject(entry.getValue().toString(), Cart.class);
                loginCartRecord.setCount(loginCartRecord.getCount().add(unloginCartRecord.getCount()));
                // 更新购物车条目的count即可
                asyncCarService.modify(_key, loginCartRecord);
                stringRedisTemplate.opsForHash().put(loginRedisKey, loginCartRecord.getSkuId().toString(), JSON.toJSONString(loginCartRecord));
            } else {
                // 否则直接添加记录即可
                Cart preAddCartRecord = JSON.parseObject(entry.getValue().toString(), Cart.class);
                stringRedisTemplate.opsForHash().put(loginRedisKey, preAddCartRecord.getSkuId().toString(), JSON.toJSONString(preAddCartRecord));
                // 对数据库操作：删除原有记录，添加新的记录
                HashMap<String, String> map = new HashMap<>();
                map.put("userId", preAddCartRecord.getUserId());
                asyncCarService.removeByMap(map);
                asyncCarService.add(preAddCartRecord);
                preAddCartRecord.setUserId(_key);
            }
        });

        // 3. 删除未登录的购物车
        stringRedisTemplate.delete(unloginRedisKey);

        // 4. 查询Redis中最终的购物车
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(loginRedisKey);
        return entries.entrySet().stream().map(entry -> {
            Cart cart = JSON.parseObject(entry.getValue().toString(), Cart.class);
            BigDecimal currentPrice = new BigDecimal(stringRedisTemplate.opsForValue().get(REDIS_SKU_PRICE_PREFIX + cart.getSkuId()));
            cart.setCurrentPrice(currentPrice);
            return cart;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateNum(Cart cart) {
        if (cart.getCount() == null || StringUtils.isBlank(cart.getCount().toString()) || cart.getSkuId() == null || StringUtils.isBlank(cart.getSkuId().toString())) {
            return;
        }

        // 1. 获取UserInfo
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userId = userInfo.getUserId();

        // 2. 判断登陆状态，未登录用userKey，登录用userId
        String key = userId;
        if (StringUtils.isBlank(userId)) {
            key = userInfo.getUserKey();
        }
        String redisKey = REDIS_CART_PREFIX + key;
        String skuId = cart.getSkuId().toString();

        // 查询Redis的记录并更新
        Cart redisCart = JSON.parseObject(stringRedisTemplate.opsForHash().get(redisKey, skuId).toString(), Cart.class);
        if (redisCart != null) {
            redisCart.setCount(cart.getCount());
            stringRedisTemplate.opsForHash().put(redisKey, skuId, JSON.toJSONString(redisCart));
        }

        // 查询数据库的记录并更新
        Cart mysqlCart = asyncCarService.query(userId, skuId);
        if (mysqlCart != null) {
            mysqlCart.setCount(cart.getCount());
            asyncCarService.modify(key, mysqlCart);
        }
    }

    @Override
    public void deleteCart(Long skuId) {
        if (skuId == null) {
            return;
        }
        // 1. 获取UserInfo
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userId = userInfo.getUserId();

        // 2. 判断登陆状态，未登录用userKey，登录用userId
        String key = userId;
        if (StringUtils.isBlank(userId)) {
            key = userInfo.getUserKey();
        }
        String redisKey = REDIS_CART_PREFIX + key;
        String skuIdStr = skuId.toString();

        // 删除Redis的记录
        stringRedisTemplate.opsForHash().delete(redisKey, skuId.toString());
        // 删除MyBatis的记录
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", key);
        map.put("skuId", skuId.toString());
        asyncCarService.removeByMap(map);
    }

    @Override
    public void updateRealTimePrice(Long skuId, BigDecimal price) {
        String key = REDIS_SKU_PRICE_PREFIX + skuId.toString();
        stringRedisTemplate.opsForValue().set(key, price.toString());
    }

}
