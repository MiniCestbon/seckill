package com.evian.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evian.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀订单表 Mapper 接口
 *
 * @author evian
 * @since 2022-05-03
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {

}
