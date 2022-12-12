package com.tony.rabbtmq.springbootrabbtmq.consumer;


import com.rabbitmq.client.Channel;
import com.tony.rabbtmq.springbootrabbtmq.config.ConfirmConfig;
import com.tony.rabbtmq.springbootrabbtmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;


//接收消息
@Component
@Slf4j
public class Consumer {


    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    private void getMessage(Message message , Channel channel) throws UnsupportedEncodingException {

        log.info("接收到的隊列消息消息 : confirm.queue消息 {}" ,
                new String(message.getBody(),"UTF-8"));

    }

}
