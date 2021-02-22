package com.atguigu.gmall.ums.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.exception.UserException;
import com.atguigu.gmall.ums.service.MqService;
import com.atguigu.gmall.ums.util.RandomUtils;
import consts.MessageConst;
import dto.ShortMessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;
import org.springframework.util.DigestUtils;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    MqService mqService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean checkUniqueness(String data, Integer type) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("phone", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                return null;
        }
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0 ? false : true;
    }

    @Override
    public void sendValidateCode(String phone, Integer type) {

        ShortMessageDto messageDto = new ShortMessageDto(type, phone);
        mqService.sendValidateCode2Mq(messageDto);
    }

    @Override
    public void userRegister(UserEntity formUserEntity, String code) {
        if (formUserEntity == null) {
            throw new UserException("表单有误, 请确认后提交!");
        }

        // 1.验证验证码
        String phone = formUserEntity.getPhone();
        if (phone == null) {
            throw new UserException("电话不能为空!");
        }
        String key = MessageConst.REGISTER_PREFIX + phone;
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        // 删除Redis中的注册用键
        stringRedisTemplate.delete(key);
        if (redisCode == null) {
            throw new UserException("验证码已过期!");
        }
        if (!redisCode.equals(code)) {
            throw new UserException("验证码不正确!");
        }

        // 2.密码加盐加密
        String salt = RandomUtils.generateRandomNumberStr(32);
        String encPassword = encryptPassword(formUserEntity.getPassword(), salt);
        formUserEntity.setPassword(encPassword);
        formUserEntity.setSalt(salt);

        // 3.设置其他相关信息
        formUserEntity.setLevelId(1l);
        formUserEntity.setCreateTime(new Date());
        formUserEntity.setIntegration(1000);
        formUserEntity.setGrowth(1000);
        formUserEntity.setStatus(1);

        // 4.保存到数据库
        this.save(formUserEntity);
    }

    @Override
    public String userLogin(String loginName, String password) {
        // 用户可能的登陆形式：username | phone | email
        if (loginName == null || password == null) {
            throw new UserException("用户名不能为空！");
        }
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginName).or().eq("phone", loginName).or().eq("email", loginName);
        List<UserEntity> list = this.list(wrapper);

        // 可能的UserList
        ArrayList<UserEntity> likelyUser = new ArrayList<>();
        list.forEach(userEntity -> {
            String dbPassword = userEntity.getPassword();
            String formEncPassword = encryptPassword(password, userEntity.getSalt());
            if (formEncPassword.equals(userEntity.getPassword())) {
                likelyUser.add(userEntity);
            }
        });

        // 判断可能的userList
        if (likelyUser.size() != 1) {
            if (likelyUser.size() == 0) {
                // 没有匹配的用户了逻辑
            } else if (likelyUser.size() > 1) {
                // 用户超过1个 联系管理员 逻辑
            }
            return null;
        }
        String userJson = JSON.toJSONString(likelyUser.get(0));

        return userJson;
    }

    /**
     * 加盐加密方法
     * @param password
     * @param salt
     * @return
     */
    private String encryptPassword(String password, String salt) {
        String password2 = password + salt;
        return DigestUtils.md5DigestAsHex(password2.getBytes(StandardCharsets.UTF_8));
    }

}