package com.evian.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evian.seckill.exception.GlobalException;
import com.evian.seckill.mapper.OrderMapper;
import com.evian.seckill.pojo.Order;
import com.evian.seckill.pojo.SeckillGoods;
import com.evian.seckill.pojo.SeckillOrder;
import com.evian.seckill.pojo.User;
import com.evian.seckill.service.IGoodsService;
import com.evian.seckill.service.IOrderService;
import com.evian.seckill.service.ISeckillGoodsService;
import com.evian.seckill.service.ISeckillOrderService;
import com.evian.seckill.utils.MD5Util;
import com.evian.seckill.utils.UUIDUtil;
import com.evian.seckill.vo.GoodsVo;
import com.evian.seckill.vo.OrderDetailVo;
import com.evian.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author evian
 * @since 2022-05-03
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderServiceImpl seckillOrderService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goods.getId()));
//        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = " + "stock_count-1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0)
        );
        if(seckillGoods.getStockCount() < 1){
            // 判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(), " ");
            return null;
        }
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        valueOperations.set("order:"+user.getId()+ ":" + goods.getId(), seckillOrder);
        return order;
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order tOrder = orderMapper.selectById(orderId);
        GoodsVo goodsVobyGoodsId = goodsService.findGoodsVoByGoodsId(tOrder.getGoodsId());
        OrderDetailVo orderDeatilVo = new OrderDetailVo();
        orderDeatilVo.setOrder(tOrder);
        orderDeatilVo.setGoodsVo(goodsVobyGoodsId);
        return orderDeatilVo;
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("secKillPath:" + user.getId() + ":" + goodsId, str, 1, TimeUnit.MINUTES);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("secKillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
