package com.evian.seckill.controller;

import com.evian.seckill.exception.GlobalException;
import com.evian.seckill.pojo.SeckillOrder;
import com.evian.seckill.pojo.User;
import com.evian.seckill.rabbitmq.MQSender;
import com.evian.seckill.service.IGoodsService;
import com.evian.seckill.service.IOrderService;
import com.evian.seckill.service.ISeckillOrderService;
import com.evian.seckill.utils.JsonUtil;
import com.evian.seckill.vo.GoodsVo;
import com.evian.seckill.vo.RespBean;
import com.evian.seckill.vo.RespBeanEnum;
import com.evian.seckill.vo.SeckillMessage;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Evian
 * @date 2022/5/3 22:04
 */
@Slf4j
@Controller
@RequestMapping("/secKill")
public class secKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
//    @Autowired
//    private RedisScript<Long> redisScript;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping(value = "/{path}/doSecKill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId){
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 判断秒杀地址
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 判断是否重复抢购 redis
        SeckillOrder secKillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if(secKillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 预减库存 stock为减1之后的值
        Long stock = valueOperations.decrement("secKillGoods:" + goodsId);
//        Long stock = (Long) redisTemplate.execute(redisScript,
//                Collections.singletonList("secKillGoods:" + goodsId), Collections.EMPTY_LIST);
        if(stock < 0){
            EmptyStockMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 消息对象
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);

//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        // 判断库存
//        if(goods.getStockCount() < 1){
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
        // 判断是否重复抢购
    //        SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
    //                .eq("user_id", user.getId())
    //                .eq("goods_id", goodsId));
        // 判断是否重复抢购 redis
//        SeckillOrder secKillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
//        if(secKillOrder != null){
//            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
//        }
//
//        Order order = orderService.secKill(user, goods);
//
//        return RespBean.success(order);
    }

    /**
     * 初始化
     * 加载商品数量
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("secKillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("getResult")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     *
     * @param user
     * @param goodsId
     * @param captcha
     * @param request
     * @return
     */
    @GetMapping(value = "/path")
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    @GetMapping(value = "/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 60, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }
}
