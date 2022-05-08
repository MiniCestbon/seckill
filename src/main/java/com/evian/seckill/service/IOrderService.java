package com.evian.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evian.seckill.pojo.Order;
import com.evian.seckill.pojo.User;
import com.evian.seckill.vo.GoodsVo;
import com.evian.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author evian
 * @since 2022-05-03
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order secKill(User user, GoodsVo goods);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
