package com.atguigu.gmall.search.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.search.entity.SearchParamVo;
import com.atguigu.gmall.search.entity.SearchResponseVo;

/**
 * Author: Administrator
 * Create: 2021/2/1
 **/
public interface SearchService {

    SearchResponseVo search(SearchParamVo searchParam);

    public void createIndex(Long spuId);

}
