package com.atguigu.gmall.auth.autoconfig;

import com.atguigu.gmall.common.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
@ConfigurationProperties(prefix = "auth.jwt")
@Component
@Data
public class AuthProperties {

    private String pubKeyPath;

    private String priKeyPath;

    private String secret;

    private String cookieName;

    private Integer expire;

    private String unick;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        String pubKeyDir = pubKeyPath.substring(0, pubKeyPath.lastIndexOf('/'));
        String priKeyDir = priKeyPath.substring(0, priKeyPath.lastIndexOf('/'));
        File pubKeyDirFile = new File(pubKeyDir);
        File priKeyDirFile = new File(priKeyDir);
        if (!pubKeyDirFile.exists()) {
            pubKeyDirFile.mkdirs();
        }
        if (!priKeyDirFile.exists()) {
            priKeyDirFile.mkdirs();
        }
        RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
        publicKey = RsaUtils.getPublicKey(pubKeyPath);
        privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

}
