package com.tony.rabbitmq.eight;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

//死信隊列 生產者
public class Producer {

    //聲名正常一般的 交換機
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();


//        //測試死信隊列TTL時間 單位ms  10000ms = 10s
//        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
//                .builder()
//                .expiration("20000")
//                .build();

        for(int i = 1 ; i<=10;i++){
            String message = "info" + i; // info1.info2.....
            //發送消息
            //1.交換機名
            //2.routingKey
            //3.附加的參數 AMQP.BasicProperties 設置
            //4.發送的消息
//            channel.basicPublish(NORMAL_EXCHANGE,"Q1_normal",true,
//                    basicProperties,message.getBytes("UTF-8"));

            channel.basicPublish(NORMAL_EXCHANGE,"Q1_normal",true,
                    null,message.getBytes("UTF-8"));

            System.out.println("生產者發消息 : " + message);

        }



    }


}
