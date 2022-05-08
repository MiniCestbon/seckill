package com.evian.seckill.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Evian
 * @date 2022/5/6 17:24
 */
@Service
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSecKillMessage(String message){
        rabbitTemplate.convertAndSend("secKillExchange", "secKill.message", message);
    }
}
