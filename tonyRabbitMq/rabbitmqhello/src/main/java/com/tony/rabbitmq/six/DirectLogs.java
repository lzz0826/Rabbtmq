package com.tony.rabbitmq.six;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;


//直接交換機 路由模式
public class DirectLogs {

    private static final String EXCHANGE_NAME = "direct_log";

    public static void main(String[] args) throws IOException, TimeoutException {

        System.out.println("生產者準備發消息 : ");

        Channel channel = RabbitMqUtils.getChannel();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            //routingKey 可以綑綁多個 但無法發送多個(路由模式)
            channel.basicPublish(EXCHANGE_NAME,"warning", MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
            System.out.println("生產者發消息" + message);
        }

    }

}
