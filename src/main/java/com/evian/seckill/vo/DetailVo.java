package com.evian.seckill.vo;

import com.evian.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品详情返回对象
 *
 * @author: Evian
 * @date 2022/5/5 18:48
 * @ClassName: DetailVo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private User user;

    private GoodsVo goodsVo;

    private int secKillStatus;

    private int remainSeconds;

}
