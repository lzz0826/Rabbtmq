package com.tony.rabbitmq.five;


import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tony.rabbitmq.uitls.RabbitMqUtils;
import com.tony.rabbitmq.uitls.SleepUitls;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs01 {


    private static final String QUEUES_NAME =  "tinytest01" ;

    private static final String EXCHANGE_NAME =  "logs" ;


    public static void main(String[] args) throws IOException, TimeoutException {

        //創建渠道
        Channel channel = RabbitMqUtils.getChannel();

        //創建交換機 exchangeDeclare()  fanout 扇出模式
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");


        /*
        * 創建臨時隊列
        * 生成一個臨時隊列 名稱隨機
        * 當消費者斷開與隊列的連接後 列隊自動刪除
        * */
        String queuesName =  channel.queueDeclare().getQueue();

        //綁定交換機與隊列 queueBind()
        channel.queueBind(queuesName,EXCHANGE_NAME,"");
        System.out.println("ReceiveLogs01 等待接收消息");


        DeliverCallback deliverCallback = (consumerTag, message)->{

            System.out.println("ReceiveLogs01 接收到的消息" + new String(message.getBody(),"UTF-8"));
        };

        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調
        channel.basicConsume(QUEUES_NAME,true,deliverCallback,CancelCallback->{});


    }


}














