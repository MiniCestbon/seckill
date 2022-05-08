package com.evian.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evian.seckill.pojo.Goods;
import com.evian.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品表 Mapper 接口
 *
 * @author evian
 * @since 2022-05-03
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
