package com.tony.rabbtmq.springbootrabbtmq.consumer;

import com.rabbitmq.client.Channel;
import com.tony.rabbtmq.springbootrabbtmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;



//消費者  延遲隊列
@Slf4j
@Component
public class DelayedQueueConsumer {



    //監聽消息
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayedMsg(Message message , Channel channel) throws UnsupportedEncodingException {
        String meg = new String(message.getBody(),"UTF-8");

        log.info("當前時間 : {} ,收到的延遲隊列得消息 : {} ",new Date().toString(),meg);


    }
}
