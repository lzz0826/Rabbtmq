package com.tony.rabbtmq.springbootrabbtmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//高級發佈確認
@Configuration
@Slf4j
public class ConfirmConfig {

    //交換機
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";

    //對列
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";


    //RoutingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";

    //交換機聲明
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
       return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }

    //交換對列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    //綁定
    @Bean()
    public Binding confirmQueueBindingconfirmExchange(@Qualifier("confirmQueue")Queue confirmQueue ,
                                                      @Qualifier("confirmExchange")DirectExchange confirmExchange) {

        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }


}
