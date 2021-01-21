package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登陆记录表
 * 
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 20:22:44
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLogEntity> {
	
}
