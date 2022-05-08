package com.evian.seckill.vo;

import com.evian.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀信息
 *
 * @author: Evian
 * @date 2022/5/6 20:54
 * @ClassName: SeckillMessage
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {

    private User user;

    private Long goodsId;
}
