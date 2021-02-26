package com.atguigu.gmall.auth;

import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import com.sun.javafx.fxml.expression.KeyPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Administrator
 * Create: 2021/2/23
 **/
public class CommonTest {

    private String secret = "123";

    String pubKeyPath = "rsa/rsa.pub";

    String priKeyPath = "rsa/rsa.pri";

    @Test
    public void testGenerateKey() throws Exception {
        String pubKeyDir = pubKeyPath.substring(0, pubKeyPath.lastIndexOf('/'));
        String priKeyDir = priKeyPath.substring(0, pubKeyPath.lastIndexOf('/'));
        File pubKeyFile = new File(pubKeyDir);
        File priKeyFile = new File(priKeyDir);

        if(!pubKeyFile.exists()){
            pubKeyFile.mkdirs();
        }
        if (!priKeyFile.exists()) {
            priKeyFile.mkdirs();
        }
        // if (!pubKeyFile.exists()) {
        //     priKeyFile.createNewFile();
        // }
        // if (!priKeyFile.exists()) {
        //     pubKeyFile.createNewFile();
        // }

        RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
    }

    @Test
    public void testGenerateJwt() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "leo");
        map.put("id", "1323654345342");
        String token = JwtUtils.generateToken(map, RsaUtils.getPrivateKey(priKeyPath), 20);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseJwt() throws Exception {
        String jwt = "eyJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoibGVvIiwiaWQiOiIxMzIzNjU0MzQ1MzQyIiwiZXhwIjoxNjE0MDUzNTY4fQ.cp2ntFndTS8qUdorytdaHTh77uT3tJMA6Pff-93h_ULZ_rYjL8VI1OqfQJasQ04zwVPyklV82NAOikTayU3a8WMMUAp3_ZJeqK36BKkRMLepFNdIsYR6ESBMrSsJx2BJTySDuFw88DAu3LneoyJGNdyKYEHswBth7QschAG5VDzgx9r-Tyi-30T-NAjfMePeEdbTaUu2A2RbUvReD9BdQVXpphn8d-i7Rex0nvVcZsOguPKRlYXMG2uMhjhKZhlGwbwlIAVfaXhC8yRH0vsNxqCwliagccWJynDNcvYxCwIAuIEgJ-6f8A9qugnKpJnhg1ri9JRX8ZaasIFVzst-Cg";
        Map<String, Object> tokenMap = JwtUtils.getInfoFromToken(jwt, RsaUtils.getPublicKey(pubKeyPath));
        System.out.println(tokenMap);
    }

}
