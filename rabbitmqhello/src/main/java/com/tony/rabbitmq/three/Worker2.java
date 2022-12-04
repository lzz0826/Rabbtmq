package com.tony.rabbitmq.three;


import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.tony.rabbitmq.uitls.RabbitMqUtils;
import com.tony.rabbitmq.uitls.SleepUitls;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//消息再手動答應時是不丟失.放回隊列中重新消費
public class Worker2 {

    //列隊名稱
    private static final String TASK_QUWUW_NAMW = "ack_queue";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {

        RabbitMqUtils rabbitMqUtils = new RabbitMqUtils();

        System.out.println("C1 等待接收消息 1 秒");

        Channel channel = rabbitMqUtils.getChannel();

        //消費者接收消息
        DeliverCallback deliverCallback = (var1,var2)->{

            String message = new String(var2.getBody(),"UTF-8");

            //沈睡1秒
            SleepUitls.sleepSecond(1);
            System.out.println("接收到的消息"+message);
            //手動應答
            //1.消息的標記 tag
            //2.是否批量應答 false : 不批量應答渠道中的消息  true: 批量 (false不批量確保還沒處理完的消息不會一起被應答)

            channel.basicAck(var2.getEnvelope().getDeliveryTag(),false);

        };
//        //消息接收被取消時 執行下面的內容
//        CancelCallback cancelCallback = (var1) ->{
//            System.out.println(var1 +"消息者取消消費接口回調邏輯");
//        };


        //消費者接收消息
        //1.消費哪個隊列
        //2.消費成功後是否要自動答應 true 代表的自動答覆 false 手動答覆
        //3.消費者未成功消回調
        //4.消費者取消消費的回調


        //設置不公平分發 (能者多勞 建議) 讓處理快的消費者不會空閒 預設0輪詢
//        int prefetchCount = 1;

        //prefetchCount 設置2之後就是 欲取值 可以指定分給渠道的消息數(堆積的 有可能因為前一條處理完了所以堆積數變化)
        //堆積後得消息因為設定欲取值所以不會分給別的消費這必須自己處理完
        int prefetchCount = 2;
        channel.basicQos(prefetchCount);

        //採用手動應答(在確定完成處理後才回應可以刪除消息)
        boolean autoAck = false;

        channel.basicConsume(TASK_QUWUW_NAMW,autoAck,deliverCallback,(cancelCallback->{
            System.out.println(cancelCallback +"消息者取消消費接口回調邏輯");
        }));

    }


}
