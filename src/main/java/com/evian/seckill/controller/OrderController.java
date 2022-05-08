package com.evian.seckill.controller;


import com.evian.seckill.pojo.User;
import com.evian.seckill.service.IOrderService;
import com.evian.seckill.vo.OrderDetailVo;
import com.evian.seckill.vo.RespBean;
import com.evian.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  前端控制器
 *
 * @author evian
 * @since 2022-05-03
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
