package com.tony.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    //隊列名稱
    private static final String QUEUE_NAME ="hello";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //創建工廠
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("tony");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調


        //聲明接收消息
//        DeliverCallback deliverCallback = new DeliverCallback() {
//            @Override
//            public void handle(String s, Delivery delivery) throws IOException {
//
//                //取得消息 body 有消息頭 消息屬性 消息體
//                System.out.println(new String(delivery.getBody()));
//            }
//        };
        DeliverCallback deliverCallback = (s, delivery)->{
//                //取得消息 body 有消息頭 消息屬性 消息體
            System.out.println(new String(delivery.getBody()));
        };



        //取消消息時間的回調
//        CancelCallback cancelCallback = new CancelCallback() {
//            @Override
//            public void handle(String s) throws IOException {
//                System.out.println("消費消息被中斷");
//            }
//        };
        CancelCallback cancelCallback = (s)->{
             System.out.println("消費消息被中斷");
        };


        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);

    }


}
