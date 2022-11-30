package com.tony.rabbitmq.six;

import com.rabbitmq.client.*;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect01 {


    private static final String EXCHANGE_NAME = "direct_log";


    public static void main(String[] args) throws IOException, TimeoutException {


        System.out.println("ReceiveLogsDirect01 準備接收消息");
        Channel channel = RabbitMqUtils.getChannel();

        //聲明一個換機 direct
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);


        //1.隊列名稱
        //2.隊列裏面的消息是否持久化(磁碟) 默認情況消息存儲在內存中(false)
        //3.該隊列是否只供一個消費者進行消費,是否進行消息共存, true可以多個消費者消費 (默認false)只能一個消費者消費
        //4.是否自動刪除 最後一個消費者斷開連接后 該隊語句是否自動刪除 true 自動刪除 (默認false)不自動刪除
        //5.其他參數
        String queuesName = "console";

        boolean durable = false;
        channel.queueDeclare(queuesName,durable,false,false,null);

        //多重綁定 routing_key
        // **注意如果該隊列沒有綁過會重起會新增 但是刪除需要到MQ刪除才會有效(代碼刪掉重起MQ還是綁定狀態)
        channel.queueBind(queuesName,EXCHANGE_NAME,"info");
        channel.queueBind(queuesName,EXCHANGE_NAME,"warning");

        DeliverCallback deliverCallback = (consumerTag,  message)->{
            System.out.println("ReceiveLogsDirect01 接收消息 :" + new String(message.getBody(),"UTF-8"));
        };

        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調

        channel.basicConsume(queuesName,true,deliverCallback,cancelCallback -> {});








    }



}
