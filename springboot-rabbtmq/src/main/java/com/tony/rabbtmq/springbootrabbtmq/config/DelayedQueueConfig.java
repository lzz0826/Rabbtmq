package com.tony.rabbtmq.springbootrabbtmq.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedQueueConfig {


    //交換機
    public static final String DELAYED_EXCHANG_NAME = "delayed.exchange";

    //隊列
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";


    //routingKey
    public static final String DELAYED_ROUTINGKEY_NAME = "delayed.routingKey";


    //聲明機換機 基於插件
    @Bean(name = "delayedExchange")
    public CustomExchange delayedExchange(){
        //其他參數 延遲類型
        Map<String,Object> arguments = new HashMap<>();
        //延遲類型 direct 因不是路由 routingKey是個固定值 所以是direct
        arguments.put("x-delayed-type","direct");
        //1.交換機名稱
        //2.交換機類型
        //3.是否需要持久化
        //4.是否需要自動刪除
        //5.其他參數
        CustomExchange customExchange = new CustomExchange(DELAYED_EXCHANG_NAME,
                "x-delayed-message",true,false,arguments
        );

        return customExchange;

    }

    //生成隊列
    @Bean(name = "delayedQueue")
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME);
    }

    //綁定
    @Bean(name = "delayedBinding")
    public Binding delayedQueueBindingDelayedExchange(
            @Qualifier("delayedQueue") Queue delayedQueue ,
            @Qualifier("delayedExchange") CustomExchange delayedExchange ){

        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTINGKEY_NAME).noargs();
    }

}




















