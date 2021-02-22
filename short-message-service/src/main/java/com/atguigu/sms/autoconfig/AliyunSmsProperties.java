package com.atguigu.sms.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
@Component
@ConfigurationProperties(prefix = "aliyun")
@Data
public class AliyunSmsProperties {

    private String accessKey;

    private String accessSecret;

    private String signName;

    private String templateCode;

}
