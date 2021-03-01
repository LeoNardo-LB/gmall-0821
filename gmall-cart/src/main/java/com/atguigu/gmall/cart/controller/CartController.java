package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.entity.CheckVo;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @ResponseBody
    @PostMapping("/updateStatus")
    public ResponseVo updateCheckStatus(@RequestBody CheckVo checkVo) {
        cartService.updateCheckStatus(checkVo.getSkuId(), checkVo.getCheck());
        return ResponseVo.ok();
    }

    @ResponseBody
    @GetMapping("/cart/checked/{userId}")
    public ResponseVo<List<Cart>> queryCheckedCarts(@PathVariable String userId) {
        List<Cart> carts = cartService.queryCheckedCart(userId);
        return ResponseVo.ok(carts);
    }

    /**
     * 添加购物车成功，重定向到购物车成功页
     * @param cart
     * @return
     */
    @GetMapping
    public String addCart(Cart cart) {
        if (cart == null || cart.getSkuId() == null) {
            throw new RuntimeException("没有选择添加到购物车的商品信息！");
        }
        this.cartService.addCart(cart);

        return "redirect:http://cart.gmall.com/addCart.html?skuId=" + cart.getSkuId();
    }

    /**
     * 跳转到添加成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId, Model model) {

        Cart cart = this.cartService.queryCartBySkuId(skuId);
        model.addAttribute("cart", cart);
        return "addCart";
    }

    /**
     * 查询购物车
     * @param model
     * @return
     */
    @GetMapping("cart.html")
    public List<Cart> queryCarts(Model model) {
        List<Cart> carts = cartService.queryCarts();
        model.addAttribute("carts", carts);

        return carts;
    }

    @PostMapping("updateNum")
    @ResponseBody
    public ResponseVo<Object> updateNum(@RequestBody Cart cart) {

        this.cartService.updateNum(cart);
        return ResponseVo.ok();
    }

    @PostMapping("deleteCart")
    @ResponseBody
    public ResponseVo<Object> deleteCart(@RequestParam("skuId") Long skuId) {

        this.cartService.deleteCart(skuId);
        return ResponseVo.ok();
    }

}