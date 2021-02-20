package com.atguigu.gmall.pms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * spu信息介绍
 * @author leonardo
 * @email tfr971018@163.com
 * @date 2021-01-18 18:32:22
 */
@Data
@Accessors(chain = true)
@TableName("pms_spu_desc")
public class SpuDescEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 商品id
     */
    @TableId(type = IdType.INPUT)
    private Long spuId;

    /**
     * 商品介绍
     */
    private String decript;

}
