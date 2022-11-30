package com.tony.rabbitmq.five;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.util.Scanner;

//發消息 扇出交換機 發送訂閱模式
public class EmitLog {

    //創建交換機名稱
    private static final String EXCHANGE_NAME =  "logs" ;

    public static void main(String[] args) throws Exception {
        System.out.println("生產者準備發消息 : ");

        Channel channel = RabbitMqUtils.getChannel();


        //創建交換機 exchangeDeclare()  fanout 扇出模式(發佈訂閱)
//        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()){

            String message = scanner.next();

            channel.basicPublish(EXCHANGE_NAME,"", MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));

            System.out.println("生產者發送消息 : "  + message);

        }




    }

}
