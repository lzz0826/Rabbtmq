package com.tony.rabbtmq.springbootrabbtmq.consumer;


//隊列 TTL 消費者

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
@Slf4j
public class DeadLetterQueueConsumer {


    // @RabbitListener(queues = "") 監聽的隊列
    @RabbitListener(queues = "QD")
    public void receiveD(Message message , Channel channel) throws UnsupportedEncodingException {
        String meg = new String(message.getBody(),"UTF-8");
        log.info("當前時間 : {} ,收到的死信隊列得消息 : {} ",new Date().toString(),meg);

    }



}









