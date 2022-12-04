package com.tony.rabbitmq.two;


import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//這是一個工作線程(相當於之前消費者)
public class Worker01 {
    //隊列的名稱
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        RabbitMqUtils rabbitMqUtils = new RabbitMqUtils();
        Channel channel = rabbitMqUtils.getChannel();


        //消息接收
        DeliverCallback deliverCallback = (var1, var2)->{

            System.out.println("接收到的消息 : " + new String(var2.getBody()));

        };

        //消息接收被取消時 執行下面的內容
        CancelCallback cancelCallback = var1->{
            System.out.println(var1+"消息者取消消費接口回調邏輯");

        };

        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調

        System.out.println("當前線程 C2 等待接收 ");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);


    }


}
