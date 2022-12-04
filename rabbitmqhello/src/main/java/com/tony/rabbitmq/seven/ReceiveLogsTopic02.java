package com.tony.rabbitmq.seven;

//交換機 主題模式

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

public class ReceiveLogsTopic02 {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queue_name = "Q2";

        boolean durable = false;

        channel.queueDeclare(queue_name,durable,false,false,null);

        channel.queueBind(queue_name,EXCHANGE_NAME,"*.*.rabbit");
        channel.queueBind(queue_name,EXCHANGE_NAME,"lazy.#");

        System.out.println("Q2 開始接收消息....");

        DeliverCallback deliverCallback = (consumerTag,  message) ->{
            System.out.println("ReceiveLogsTopic02 接收消息 :" + new String(message.getBody(),"UTF-8"));
            System.out.println("接收隊列 : "+ queue_name+"  routing_key : " +message.getEnvelope().getRoutingKey());
        };

        channel.basicConsume(queue_name,true,deliverCallback,cancelCallback->{});


    }



}










