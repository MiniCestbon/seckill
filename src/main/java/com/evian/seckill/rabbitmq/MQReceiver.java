package com.evian.seckill.rabbitmq;

import com.evian.seckill.pojo.SeckillOrder;
import com.evian.seckill.pojo.User;
import com.evian.seckill.service.IGoodsService;
import com.evian.seckill.service.IOrderService;
import com.evian.seckill.utils.JsonUtil;
import com.evian.seckill.vo.GoodsVo;
import com.evian.seckill.vo.SeckillMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Evian
 * @date 2022/5/6 17:24
 */
@Service
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 处理消息
     * @param message
     */
    @RabbitListener(queues = "secKillQueue")
    public void receive(String message) {
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return;
        }
        // 下单操作
        orderService.secKill(user, goodsVo);
    }
}
