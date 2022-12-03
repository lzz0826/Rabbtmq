package com.tony.rabbitmq.eight;


import com.rabbitmq.client.*;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

//死信隊列 消費者01
public class Consumer01 {


    //聲名正常一般的 隊列 交換機
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String NORMAL_QUEUE ="normal_queue";


    //聲名死信 隊列 交換機
    private static final String DEAD_EXCHANGE = "dead_exchange";

    private static final String DEAD_QUEUE = "dead_queue";



    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        //聲明交換機 一般交換機 死信交換機 (DIRECT)
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE ,BuiltinExchangeType.DIRECT);



        boolean durable = false;
        //聲明一般隊列


        channel.queueDeclare(NORMAL_QUEUE,durable,false,false,null);




        //聲明死信隊列
        channel.queueDeclare(DEAD_QUEUE,durable,false,false,null);


        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"Q1_normal");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"Q2_dead");

        DeliverCallback deliverCallback = (consumerTag, message)->{
            String callbackMessage = new String(message.getBody(),"UTF-8");
            System.out.println("Consumer01 今收到的消息" + callbackMessage);
        };

        CancelCallback cancelCallback = (consumerTag)->{
            System.out.println("消費者被取消");
        };


        channel.basicConsume(NORMAL_QUEUE,true,deliverCallback,cancelCallback);



    }

}
