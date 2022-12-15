package com.tony.rabbtmq.springbootrabbtmq.consumer;

//報警消費者

import com.rabbitmq.client.Channel;
import com.tony.rabbtmq.springbootrabbtmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
public class WarningConsumer {

//接收報警的消息
    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE_NAME)
    public void receiveWarningMsg(Message message , Channel channel) throws UnsupportedEncodingException {
        String msg = new String(message.getBody(),"UTF-8");
        log.error("報警發現不可路由的消息 :  {}" ,msg);

    }


}
