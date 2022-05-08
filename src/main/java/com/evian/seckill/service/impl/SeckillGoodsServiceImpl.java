package com.evian.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evian.seckill.mapper.SeckillGoodsMapper;
import com.evian.seckill.pojo.SeckillGoods;
import com.evian.seckill.service.ISeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀商品表 服务实现类
 * </p>
 *
 * @author evian
 * @since 2022-05-03
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements ISeckillGoodsService {

}
