package com.tony.rabbtmq.springbootrabbtmq.controller;


import com.tony.rabbtmq.springbootrabbtmq.config.ConfirmConfig;
import com.tony.rabbtmq.springbootrabbtmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//開始發消息 測試確認

@RestController()
@RequestMapping("/confirm")
@Slf4j
public class ProducerController {


    @Autowired
    RabbitTemplate rabbitTemplate;



    //發消息
    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable("message") String message ){

        CorrelationData correlationData1 = new CorrelationData();
        //測試沒異常的情況
        correlationData1.setId("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY,message+"KEY1",correlationData1);
        log.info("發送內容正確的 : {}" ,message+"KEY1");


////        測試交換機死掉
//        CorrelationData correlationData2 = new CorrelationData();
//        correlationData2.setId("2");
//        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME+"123",
//                ConfirmConfig.CONFIRM_ROUTING_KEY,message,correlationData2);
//        log.info("發送交換機異常的 : {}" ,message);





        //測試信道機死掉 (以設置消息回退:信道異常時退回給生產者)
        CorrelationData correlationData3 = new CorrelationData();
        correlationData3.setId("3");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY+"2",message+"KEY12",correlationData3);
        log.info("發送信道異常的 ROUTINGKEY12 : {}" ,message+"KEY12");

    }



}
