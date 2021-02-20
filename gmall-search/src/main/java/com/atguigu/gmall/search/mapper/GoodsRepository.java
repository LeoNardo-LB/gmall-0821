package com.atguigu.gmall.search.mapper;

import com.atguigu.gmall.search.entity.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Author: Administrator
 * Create: 2021/1/30
 **/
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {

}
