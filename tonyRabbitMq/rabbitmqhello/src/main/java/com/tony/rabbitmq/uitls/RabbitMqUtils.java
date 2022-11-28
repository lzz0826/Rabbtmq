package com.tony.rabbitmq.uitls;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

;import java.io.IOException;
import java.util.concurrent.TimeoutException;

//此類為連接工場創建信道的工具類
public class RabbitMqUtils {


    public static Channel getChannel() throws IOException, TimeoutException {
        //創建一個工廠
        ConnectionFactory factory = new ConnectionFactory();
        //設置ip
        factory.setHost("127.0.0.1");
        //設置用戶名
        factory.setUsername("tony");
        //密碼
        factory.setPassword("123");
        //設置連接
        Connection connection = factory.newConnection();
        //獲取渠道
        Channel channel = connection.createChannel();
        return channel;

    }


}
