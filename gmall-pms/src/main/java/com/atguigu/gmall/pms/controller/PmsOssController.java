package com.atguigu.gmall.pms.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Author: Administrator
 * Create: 2021/1/19
 **/
@RestController
@RequestMapping("/pms/oss")
public class PmsOssController {

    String accessId = "LTAI4G6L2XjtwzWXC4xwaanQ"; // 请填写您的AccessKeyId。

    String accessKey = "QDttnNJ5RkVBrFm0TGj4RZEy3fnAEp"; // 请填写您的AccessKeySecret。

    String endpoint = "oss-cn-shanghai.aliyuncs.com"; // 请填写您的 endpoint。

    String bucket = "java0821-gmall"; // 请填写您的 bucketname 。

    String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint

    // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
    // String callbackUrl = "http://88.88.88.88:8888";

    String dir = ""; // 用户上传文件时指定的前缀。

    @GetMapping("policy")
    public ResponseVo<Object> policy() {

        // 设置dir
        String timeFormat = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String randomStr = UUID.randomUUID().toString().replace("-", "");
        dir = timeFormat + "/" + randomStr;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            return new ResponseVo<>().ok(respMap);
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return null;
    }

}
