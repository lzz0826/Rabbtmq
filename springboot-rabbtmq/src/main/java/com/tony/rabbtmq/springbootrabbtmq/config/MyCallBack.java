package com.tony.rabbtmq.springbootrabbtmq.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//設置回調接口
// 消息回調 implements  RabbitTemplate.ConfirmCallback
//需要在 application.properties 設置
//spring.rabbitmq.publisher-confirm-type=correlated

//設置回退接口(交換機透過ke給信道時 信道異常死掉 退回給生產者)

@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnsCallback {

    @Autowired
    RabbitTemplate rabbitTemplate;

    //實現的ConfirmCallback是內部椄口 實現類MyCallBack 不再對象RabbitTemplate裡
    //之後RabbitTemplate掉內部接口ConfirmCallback會調不到 MyCallBack實現類(需要注入)

    //1.@ComponentMyCallBack 2. @AutowiredRabbitTemplate 3. @PostConstruct init()
    //需要有順序不然會空指針
    @PostConstruct
    public void init(){
        //當前類 MyCallBack = this
        //注入ConfirmCallback
        rabbitTemplate.setConfirmCallback(this);
        //注入ReturnsCallback
        rabbitTemplate.setReturnCallback(this);
        /**
         * true：
         * 交换机无法将消息进行路由时，会将该消息返回给生产者
         * false：
         * 如果发现消息无法进行路由，则直接丢弃
         */
        rabbitTemplate.setMandatory(true);

    }

    /**
     * 交換機確認回調方法
     * 1.發消息 交換機接收到了 回調
     * 1.1 correlationData 保存回調消息的ID 及相關訊息
     * 1.2 ack 交換機接收到消息 true
     * 1.3 cause null 成功沒有原因
     *
     * 2. 發消息 交換機接收消息失敗了 回調
     * 2.1 correlationData 保存回調消息的ID 及相關訊息
     * 2.2 ack 交換機接收到消息 false
     * 2.3 cause 失敗的原因
    */

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //防止空指針 有id的話取 correlationData.getId() 否則拿""
        String id = correlationData !=null ? correlationData.getId() : "";
        //如果(交換機)有接到消息
        if(ack){
            log.info("交換機已經收到消息id為 : {} ",id);
            //如果(交換機)沒接到消息
        }else {
            log.info("交換機還未收id為 : {} 的消息 原因 : {}  ",id,cause);
        }
    }

    //可以在當消息傳遞過程中不可達到目的地時將消息返回給生產者
    //只有不可以達到目的地的時候 才進行回退(成功不會)
//    /**
//     * Returned message callback.
//     * @param message the returned message.
//     * @param replyCode the reply code.
//     * @param replyText the reply text.
//     * @param exchange the exchange.
//     * @param routingKey the routing key.
//     */
//    當消息無法路由的時候的回調方法
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String
            exchange, String routingKey) {
        log.error("消息 {}, 被交換機 {} 退回，退回原因 :{}, 路由key:{}",new
                String(message.getBody()),exchange,replyText,routingKey);

    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {

    }



}











