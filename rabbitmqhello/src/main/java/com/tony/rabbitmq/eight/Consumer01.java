package com.tony.rabbitmq.eight;


import com.rabbitmq.client.*;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.util.HashMap;
import java.util.Map;

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


        //聲明普通隊列
        Map<String,Object> arguments = new HashMap<>();
        //正常隊列設置死信換機
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //設置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","Q2_dead");
//        //設置過期時間 (也可以在生產者在發消息時設定 一般來說是在生產者設定 可以設定不同的時間) 毫秒 10s=1000ms
//        arguments.put("x-message-ttl",10000);

//        //------------
//        //測試死信長度限制 有修改需要先刪除隊列
//        //設置正常隊列的長度限制
//        arguments.put("x-max-length",6);
//        //------------


        boolean durable = false;

        //聲明普通隊列
        channel.queueDeclare(NORMAL_QUEUE,durable,false,false,arguments);
        //聲明死信隊列
        channel.queueDeclare(DEAD_QUEUE,durable,false,false,null);

        //綁定普通交換機與普通隊列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"Q1_normal");
        //綁定死信交換機與死信隊列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"Q2_dead");

        System.out.println("Consumer01 等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag, message)->{
            String callbackMessage = new String(message.getBody(),"UTF-8");

            //測試死信隊列消息被拒絕 需要手動答應
            if(callbackMessage.equals("info5")){
                //取得此訊息的標邊不放回隊列 (就會變成死信)
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
                System.out.println("Consumer01 接收到的消息 :"+callbackMessage+ "是被拒絕的");

            } else {
                System.out.println("Consumer01 接收到的消息 : " + callbackMessage);
                //不批量確認
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }
//            System.out.println("Consumer01 接收到的消息 : " + callbackMessage);

        };
        CancelCallback cancelCallback = (consumerTag)->{
            System.out.println("消費者被取消");
        };
        //測試死信 手動應答(如果自動應答就不存在拒絕的問題)
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,cancelCallback);
//        channel.basicConsume(NORMAL_QUEUE,true,deliverCallback,cancelCallback);



    }

}













