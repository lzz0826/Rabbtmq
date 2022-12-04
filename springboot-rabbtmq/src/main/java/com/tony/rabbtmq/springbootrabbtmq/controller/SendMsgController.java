package com.tony.rabbtmq.springbootrabbtmq.controller;


//發送延遲消息

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {


    @Autowired
    RabbitTemplate rabbitTemplate;

    //開始發消息
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String message){

        log.info("當前時間 : {} , 發送一條訊息給兩個TTL隊列 : {} " , new Date().toString(),message);

        //發送消息 根據交換機和routingKey決定訊息給哪個隊列  convertAndSend(exchange,routingKey,Object)
        //10秒的
        rabbitTemplate.convertAndSend("X","XA","來至 ttl為10 秒的生訊息"+message);

        //40秒的
        rabbitTemplate.convertAndSend("X","XB","來至 ttl為40 秒的生訊息"+message);

    }

}











