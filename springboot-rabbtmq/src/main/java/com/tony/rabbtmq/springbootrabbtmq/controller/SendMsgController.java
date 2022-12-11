package com.tony.rabbtmq.springbootrabbtmq.controller;


//發送延遲消息

import com.tony.rabbtmq.springbootrabbtmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
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


    //開始發消息 消息TTL
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sedMsg(@PathVariable("message") String message ,
            @PathVariable("ttlTime") String ttlTime){
        log.info("當前時間 : {} , 發送一條時常{}訊息給隊列QC : " ,
                new Date().toString(),ttlTime,message);
        //MessagePostProcessor 設定參數
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //設置時間
                message.getMessageProperties().setExpiration(ttlTime);
                return message;
            }
        };

        rabbitTemplate.convertAndSend("X","XC",message,messagePostProcessor);


    }


    //延遲隊列基於插件
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sedDelayedMsg(@PathVariable String message , @PathVariable Integer delayTime ){
        log.info("當前時間 : {} , 發送一條時常{}訊息給延遲隊列delayed.queue : " ,
                new Date().toString(),delayTime,message);

        //MessagePostProcessor 設定參數
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //設置延遲時間 毫秒
                message.getMessageProperties().setDelay(delayTime);
                return message;
            }
        };

        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANG_NAME, DelayedQueueConfig.DELAYED_ROUTINGKEY_NAME,message,messagePostProcessor);

    }

}











