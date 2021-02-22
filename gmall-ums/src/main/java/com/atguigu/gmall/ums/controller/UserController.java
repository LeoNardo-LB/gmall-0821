package com.atguigu.gmall.ums.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 用户表
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 20:22:44
 */
@Api(tags = "用户表 管理")
@RestController
@RequestMapping("ums/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 检查唯一性
     * @param data
     * @param type
     * @return 可用返回true，不可用返回false
     */
    @GetMapping("check/{data}/{type}")
    public ResponseVo<Boolean> checkUniqueness(@PathVariable String data, @PathVariable Integer type) {
        Boolean isUnique = userService.checkUniqueness(data, type);
        return ResponseVo.ok(isUnique);
    }

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseVo<Void> sendValidateCode(@RequestParam("phone") String phone, @RequestParam(defaultValue = "3") Integer type) {
        userService.sendValidateCode(phone, type);
        return ResponseVo.ok();
    }

    /**
     * 用户注册
     * @param userEntity
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseVo<String> userRegister(UserEntity userEntity, @RequestParam("code") String code) {
        try {
            userService.userRegister(userEntity, code);
        } catch (RuntimeException e) {
            return ResponseVo.fail(e.getMessage());
        }
        return ResponseVo.ok();
    }

    @GetMapping("/query")
    public ResponseVo<String> userLogin(@RequestParam("loginName") String loginName,
                                        @RequestParam("password") String password) {
        String userJson = userService.userLogin(loginName, password);
        return ResponseVo.ok(userJson);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryUserByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = userService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }

    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id) {
        UserEntity user = userService.getById(id);

        return ResponseVo.ok(user);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody UserEntity user) {
        userService.save(user);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody UserEntity user) {
        userService.updateById(user);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        userService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
