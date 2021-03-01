package com.atguigu.gmall.scheduled.handler;

import com.atguigu.gmall.scheduled.service.CartPriceSyncService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Author: Administrator
 * Create: 2021/2/27
 **/
@Component
public class SyncJobHandler {

    @Autowired
    CartPriceSyncService cartPriceSyncService;

    @XxlJob("pricdSyncJob")
    public ReturnT<String> pricdSyncJob(String param) {
        try {
            cartPriceSyncService.syncPrice();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

}
