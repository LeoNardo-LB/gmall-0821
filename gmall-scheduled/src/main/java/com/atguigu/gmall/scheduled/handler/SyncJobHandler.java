package com.atguigu.gmall.scheduled.handler;

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
    StringRedisTemplate stringRedisTemplate;

    @XxlJob("pricdSyncJob")
    public ReturnT<String> pricdSyncJob(String param) {

        return ReturnT.SUCCESS;
    }

}
