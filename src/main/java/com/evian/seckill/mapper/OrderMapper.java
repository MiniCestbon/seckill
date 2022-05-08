package com.evian.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evian.seckill.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 *  Mapper 接口
 *
 * @author evian
 * @since 2022-05-03
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
