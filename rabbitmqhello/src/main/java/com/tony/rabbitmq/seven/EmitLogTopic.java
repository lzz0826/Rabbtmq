package com.tony.rabbitmq.seven;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.tony.rabbitmq.five.EmitLog;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//創建生者
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

//        Q1-->綁定的是
//             中間帶 orange 帶 3 個單詞的字符串(*.orange.*)
//        Q2-->綁定的是
//             最後一個單詞是 rabbit 的 3 個單詞(*.*.rabbit)
//             第一個單詞是 lazy 的多個單詞(lazy.#)


        Map<String,String> bindKey = new HashMap<>();

        bindKey.put("quick.orange.rabbit","被隊列 Q1Q2 接收到 quick.orange.rabbit");
        bindKey.put("lazy.orange.elephant","被隊列 Q1Q2 接收到 lazy.orange.elephant");
        bindKey.put("quick.orange.fox","被隊列 Q1 接收到 quick.orange.fox");
        bindKey.put("lazy.brown.fox","被隊列 Q2 接收到 lazy.brown.fox");
        bindKey.put("lazy.pink.rabbit","雖然滿足兩個綁定但只被隊列 Q2 接收一次 lazy.pink.rabbit" );
        bindKey.put("quick.brown.fox","不匹配任何綁定不會被任何隊列接收到會被丟棄 quick.brown.fox");
        bindKey.put("quick.orange.male.rabbit","是四個單詞不匹配任何綁定會被丟棄 quick.orange.male.rabbit");
        bindKey.put("lazy.orange.male.rabbit","是四個單詞但匹配 Q2 lazy.orange.male.rabbit");

        for(Map.Entry<String,String> bindingKeyEntry : bindKey.entrySet()){
            String routingKey = bindingKeyEntry.getKey();
            String message = bindingKeyEntry.getValue();

            //發送消息
            //1.交換機名
            //2.routingKey
            //3.附加的參數 MessageProperties
            //4.發送的消息
            channel.basicPublish(EXCHANGE_NAME,routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));
            System.out.println("發送消息 :"+message);

        }



    }


}
