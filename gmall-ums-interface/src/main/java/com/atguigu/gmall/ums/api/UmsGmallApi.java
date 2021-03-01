package com.atguigu.gmall.ums.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
public interface UmsGmallApi {

    @GetMapping("ums/user/query")
    public ResponseVo<String> userLogin(@RequestParam("loginName") String loginName, @RequestParam("password") String password);

    /**
     * 根据userId 查询 收货信息列表
     */
    @GetMapping("ums/useraddress/query/userId/{userId}")
    public ResponseVo<List<UserAddressEntity>> queryByUserId(@PathVariable String userId);

    /**
     * 根据userId获取用户信息
     * @param id
     * @return
     */
    @GetMapping("ums/user/{id}")
    public ResponseVo<UserEntity> queryUserById(@PathVariable("id") Long id);

}
