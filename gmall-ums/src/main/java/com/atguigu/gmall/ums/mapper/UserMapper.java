package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 20:22:44
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
