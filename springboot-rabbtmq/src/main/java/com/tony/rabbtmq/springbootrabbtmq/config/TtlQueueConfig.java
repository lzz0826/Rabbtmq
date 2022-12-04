package com.tony.rabbtmq.springbootrabbtmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

//Ttl 配置文件類
//1.聲明交換際
//2.聲明隊列
//3.綁定關西
@Configuration
public class TtlQueueConfig {

    //聲明普通交換機
    private static final String X_EXCHANGE = "X";
    //聲明死信交換機
    private static final String Y_DEAD_LETTER_EXCHANGE ="Y";

    //聲明普通隊列 10s
    private static final String QA_QUEUE ="QA";

    //聲明普通隊列 40s
    private static final String QB_QUEUE ="QB";

    //聲明死信隊列
    private static final String QD_DEAD_LETTER_QUEUE ="QD";


    //--------------聲明交換機-----------------
    //聲明xExchange 別名=方法名  直接交換機
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }
    //聲明yExchange 死信交換機
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }


    //--------------隊列-----------------

    //聲明普通隊列 TTL 10s
    @Bean("queueA")
    public Queue queueA(){
        //初始map長度 ˇ
        Map<String,Object> arguments = new HashMap<>(3);
        //設置死信交換機
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //設置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //設置TTL 單位是 ms
        arguments.put("x-message-ttl",10000);

        return QueueBuilder.durable(QA_QUEUE).withArguments(arguments).build();
    }

    //聲名普通隊列 TTL 40s
    @Bean("queueB")
    public Queue queueB(){
        //初始map長度 ˇ
        Map<String,Object> arguments = new HashMap<>(3);
        //設置死信交換機
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //設置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //設置TTL 單位是 ms
        arguments.put("x-message-ttl",40000);

        return QueueBuilder.durable(QB_QUEUE).withArguments(arguments).build();
    }

    //聲明普通隊列
    @Bean("queueD")
    public Queue QueueD(){
        return QueueBuilder.durable(QD_DEAD_LETTER_QUEUE).build();
    }

    //--------------綁定關係-----------------


    //使用 @Qualifier() 使用別名
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    @Bean
    public Binding queueDBindingY(@Qualifier("queueD") Queue queueD,
                                  @Qualifier("yExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueD).to(xExchange).with("YD");
    }







}












