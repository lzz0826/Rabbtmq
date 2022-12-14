package com.tony.rabbitmq.two;

//生產者 發送大量消息

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Task01 {
    //列隊名稱

    private static final String QUEUE_NAME ="hello";


    //發大量消息
    public static void main(String[] args) throws IOException, TimeoutException {

        RabbitMqUtils rabbitMqUtils = new RabbitMqUtils();

        Channel channel =rabbitMqUtils.getChannel();

        //生成一個隊列 .queueDeclare參數
        //1.隊列名稱
        //2.隊列裡面的消息是否持久化(磁碟) 默認情況消息存儲在內存中(false)
        //3.該隊列是否只供一個消費者進行消費,是否進行消息共存, true可以多個消費者消費 (默認false)只能一個消費者消費
        //4.是否自動刪除 最後一個消費者斷開連接後 該隊語句是否自動刪除 true 自動刪除 (默認false)不自動刪除
        //5.其他參數
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);


        //從控制台發消息

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){

            String message = scanner.next();
            //發送一個消息 .basicPublish()參數
            //1.發送到哪個交換機
            //2.路由的Key值是哪個 本次是隊列得名稱
            //3.其他參數訊息
            //4.發送消息的消息體 (需要二進制)
            //創建聲明
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());

            System.out.println("發送消息完成 : "+message);

        }

    }

}
