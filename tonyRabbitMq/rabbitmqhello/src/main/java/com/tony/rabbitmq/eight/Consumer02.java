package com.tony.rabbitmq.eight;


import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

//消費死信隊列裡的信息
public class Consumer02 {

    //死信隊列
    private static final String DEAD_QUEUE = "dead_queue";



    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("Consumer02 等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag, message)->{
            String callbackMessage = new String(message.getBody(),"UTF-8");
            System.out.println("Consumer02 接收到的消息 : " + callbackMessage);
        };

        CancelCallback cancelCallback = (consumerTag)->{
            System.out.println("消費者被取消");
        };


        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,cancelCallback);





    }


}
