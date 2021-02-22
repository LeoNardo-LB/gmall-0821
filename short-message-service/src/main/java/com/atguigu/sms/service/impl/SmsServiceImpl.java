package com.atguigu.sms.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.sms.autoconfig.AliyunSmsProperties;
import com.atguigu.sms.service.SmsService;
import com.atguigu.sms.util.RandomUtils;
import consts.MessageConst;
import dto.ShortMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Author: Administrator
 * Create: 2021/2/21
 **/
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    AliyunSmsProperties aliyunSmsProperties;

    @Override
    public boolean validateCodeOperation(ShortMessageDto shortMessageDto) {

        // 生成验证码
        String validateCode = RandomUtils.generateRandomNumberStr(MessageConst.VALIDATE_CODE_LENGTH);

        // 将验证码存到Redis中
        String key = "";
        switch (shortMessageDto.getMessageType()) {
            case MessageConst.LOGIN_TYPE:
                key = MessageConst.LOGIN_PREFIX;
                break;
            case MessageConst.REGISTER_TYPE:
                key = MessageConst.REGISTER_PREFIX;
                break;
            case MessageConst.DEFAULT_TYPE:
                key = MessageConst.DEFAULT_PREFIX;
                break;
            default:
                return false;
        }
        key += shortMessageDto.getPhoneNumber();
        stringRedisTemplate.opsForValue().set(key, validateCode, 600, TimeUnit.SECONDS);

        // 调用发送服务
        return sendValidateCode(shortMessageDto.getPhoneNumber(), validateCode);
    }

    private boolean sendValidateCode(String phoneNumber, String validateCode) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliyunSmsProperties.getAccessKey(), aliyunSmsProperties.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", aliyunSmsProperties.getSignName());
        request.putQueryParameter("TemplateCode", aliyunSmsProperties.getTemplateCode());
        request.putQueryParameter("TemplateParam", "{'number': '" + validateCode + "'}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
            return false;
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
