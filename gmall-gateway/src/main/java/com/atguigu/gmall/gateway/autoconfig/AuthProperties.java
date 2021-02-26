package com.atguigu.gmall.gateway.autoconfig;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PublicKey;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
@ConfigurationProperties(prefix = "auth.jwt")
@Data
@Component
public class AuthProperties {

    private String pubKeyPath;

    private String cookieName;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        File file = new File(pubKeyPath);
        if (!file.exists()) {
            throw new RuntimeException("公钥丢失!");
        }
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }

}
