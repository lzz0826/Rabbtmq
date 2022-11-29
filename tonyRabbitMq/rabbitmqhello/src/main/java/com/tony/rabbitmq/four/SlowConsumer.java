package com.tony.rabbitmq.four;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.tony.rabbitmq.uitls.RabbitMqUtils;
import com.tony.rabbitmq.uitls.SleepUitls;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SlowConsumer {


    private static final String QUEUES_NAME = "tonyTest";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtils.getChannel();


        System.out.println("BB 慢的消费者 30 秒等待接收");
        //成功接收消息
        DeliverCallback deliverCallback = ( consumerTag, message) ->{
            SleepUitls.sleepSecond(30);
            String messageBody = new String(message.getBody(),"UTF-8");
            System.out.println("BB 收到了" + messageBody);
            //手動應答 basicAck()
            //1.消息的標記 tag
            //2.是否批量應答 false : 不批量應答渠道中的消息  true: 批量 (false不批量確保還沒處理完的消息不會一起被應答)
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        //失败接收消息
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println(consumerTag +"消息者取消消費接口回調邏輯");
        };

        //设置步公平分发
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        //手动答覆
        boolean autoAck = false;

        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調
        channel.basicConsume(QUEUES_NAME,autoAck ,deliverCallback,cancelCallback);



    }


}
