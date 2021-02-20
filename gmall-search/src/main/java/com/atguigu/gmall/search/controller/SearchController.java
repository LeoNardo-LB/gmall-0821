package com.atguigu.gmall.search.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.search.entity.SearchParamVo;
import com.atguigu.gmall.search.entity.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Author: Administrator
 * Create: 2021/2/1
 **/
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping
    public String search(SearchParamVo paramVo, Model model) throws IOException {

        SearchResponseVo responseVo = searchService.search(paramVo);

        model.addAttribute("response", responseVo);
        model.addAttribute("searchParam", paramVo);
        return "search";
    }

}
