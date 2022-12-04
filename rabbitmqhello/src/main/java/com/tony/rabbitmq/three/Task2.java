package com.tony.rabbitmq.three;


//消息再手動答應時是不丟失.放回隊列中重新消費

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Task2 {


    //聲明隊列名稱
    private static final String TASK_QUWUW_NAMW ="ack_queue";



    public static void main(String[] args) throws IOException, TimeoutException {

        RabbitMqUtils rabbitMqUtils = new RabbitMqUtils();

        Channel channel = rabbitMqUtils. getChannel();
        //生成一個隊列 .queueDeclare參數
        //1.隊列名稱
        //2.隊列裡面的消息是否持久化(磁碟) 默認情況消息存儲在內存中(false)
        //3.該隊列是否只供一個消費者進行消費,是否進行消息共存, true可以多個消費者消費 (默認false)只能一個消費者消費
        //4.是否自動刪除 最後一個消費者斷開連接後 該隊語句是否自動刪除 true 自動刪除 (默認false)不自動刪除
        //5.其他參數

        //聲明隊列

        boolean durable = true;//需要讓 Queue(隊列) 進行持久化 防止mq當機(或重啟)後隊列消失
        // 如果原先已有同名的false隊列會報錯 須先刪除

        channel.queueDeclare(TASK_QUWUW_NAMW,durable,false,false,null);


        //發送一個消息 .basicPublish()參數
        //1.發送到哪個交換機
        //2.路由的Key值是哪個 本次是隊列得名稱
        //3.其他參數訊息
        //4.發送消息的消息體 (需要二進制)
        //創建聲明

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String message = scanner.next();

            //MessageProperties.PERSISTENT_TEXT_PLAIN 設置生產者發送消息為持久化(要求存在磁碟上)
            channel.basicPublish("",TASK_QUWUW_NAMW, MessageProperties.PERSISTENT_TEXT_PLAIN
                    ,message.getBytes("UTF-8"));
            System.out.println("生產者發送消息 : " + message );
        }


    }

}
