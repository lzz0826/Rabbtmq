package com.tony.rabbtmq.springbootrabbtmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

//高級發佈確認
@Configuration
@Slf4j
public class ConfirmConfig {

    //交換機
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    //備份交換機
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";



    //對列
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";


    //備份對列 警報對列
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    public static final String WARNING_QUEUE_NAME = "warning.queue";

    //RoutingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";



    //交換機聲明
    //確認交換機
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){


        //使用 ExchangeBuilder.directExchange .withArgument() 聲明參數 "alternate-exchange" 備份交換機
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME).durable(true)
                .withArgument("alternate-exchange",BACKUP_EXCHANGE_NAME)
                .build();

//       return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }

    //備份交換機
    @Bean("backupExchange")
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }


    //聲明對列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    //備份對列 報警對列

    @Bean("backupQueue")
    public Queue backupQueue(){
        return  QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }
    @Bean("warningQueue")
    public Queue warningQueue(){
        return  QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }



    //綁定
    @Bean()
    public Binding confirmQueueBindingConfirmExchange(@Qualifier("confirmQueue")Queue confirmQueue ,
                                                      @Qualifier("confirmExchange")DirectExchange confirmExchange) {

        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }

    //綁定備份對列和備份交換機
    @Bean()
    public Binding backupQueueBindingBackupExchange(@Qualifier("backupQueue") Queue backupQueue,
                                                    @Qualifier("backupExchange")FanoutExchange backupExchange){
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    //綁定警報對列和備份交換機
    @Bean()
    public Binding warningQueueBindingWarningQueue(@Qualifier("warningQueue")Queue warningQueue,
                                                    @Qualifier("backupExchange")FanoutExchange backupExchange ){
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }


}
