package com.evian.seckill.vo;

import com.evian.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情返回对象
 *
 * @author: Evian
 * @date 2022/5/5 21:01
 * @ClassName: OrderDetailVo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {

    private Order order;

    private GoodsVo goodsVo;
}
