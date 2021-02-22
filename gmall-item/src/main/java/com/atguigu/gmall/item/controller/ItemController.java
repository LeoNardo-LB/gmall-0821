package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Author: Administrator
 * Create: 2021/2/20
 **/
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
    public String load(@PathVariable("skuId") Long skuId, Model model) {

        ItemVo itemVo = this.itemService.itemDetailsPackaging(skuId);
        System.out.println(itemVo);
        model.addAttribute("itemVo", itemVo);

        return "item";
    }

}
