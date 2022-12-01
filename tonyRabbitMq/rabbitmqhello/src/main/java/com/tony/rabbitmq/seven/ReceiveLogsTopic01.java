package com.tony.rabbitmq.seven;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//交換機 主題模式
public class ReceiveLogsTopic01 {

    private static final String EXCHANGE_NAME = "topic_logs";


    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMqUtils.getChannel();

        //聲明一個交換機
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);



        String quesesName = "Q1";
        boolean durable = false;

        //1.隊列名稱
        //2.隊列裏面的消息是否持久化(磁碟) 默認情況消息存儲在內存中(false)
        //3.該隊列是否只供一個消費者進行消費,是否進行消息共存, true可以多個消費者消費 (默認false)只能一個消費者消費
        //4.是否自動刪除 最後一個消費者斷開連接后 該隊語句是否自動刪除 true 自動刪除 (默認false)不自動刪除
        //5.其他參數
        channel.queueDeclare(quesesName,durable,false,false,null);

        //多重綁定 routing_key
        // **注意如果該隊列沒有綁過會重起會新增 但是刪除需要到MQ刪除才會有效(代碼刪掉重起MQ還是綁定狀態)
        //1.隊列名
        //2.交換機名
        //3.routing_key
        channel.queueBind(quesesName,EXCHANGE_NAME,"*.orange.*");

        DeliverCallback deliverCallback =(consumerTag,  message)->{
            System.out.println("ReceiveLogsTopic01 接收消息 :" + new String(message.getBody(),"UTF-8"));
            System.out.println("接收隊列 : "+ quesesName+"  routing_key : " +message.getEnvelope().getRoutingKey());
        };

        System.out.println("Q1 開始接收消息....");


        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調
        channel.basicConsume(quesesName,true,deliverCallback,cancelCallback->{});


    }

}