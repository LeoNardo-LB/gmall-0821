package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.index.service.LockTestService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/3
 **/
@Controller
public class IndexController {

    @Autowired
    IndexService indexService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    LockTestService lockTestService;

    @GetMapping
    public String toIndex(Model model) {
        List<CategoryEntity> categoryEntities = indexService.queryLevel0Categories();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    @GetMapping("/index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> queryLevel23Categories(@PathVariable Long pid) {
        List<CategoryEntity> list = indexService.queryLevel23CategoriesAnnotation(pid);
        return ResponseVo.ok(list);
    }

    @GetMapping("/index/test/lock")
    @ResponseBody
    public ResponseVo testLock() throws InterruptedException {
        lockTestService.testRedissonLock();
        return ResponseVo.ok();
    }

}
