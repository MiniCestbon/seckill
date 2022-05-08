package com.evian.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evian.seckill.pojo.SeckillOrder;
import com.evian.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author evian
 * @since 2022-05-03
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
