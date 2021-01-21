package com.atguigu.gmall.controller.Api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.controller.Dto.SmsSaveDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Author: Administrator
 * Create: 2021/1/20
 **/
public interface GmallSmsApi {

    @PostMapping ("sms/skubounds/save")
    public ResponseVo saveSkuBounds(@RequestBody SmsSaveDto smsSaveDto);

}
