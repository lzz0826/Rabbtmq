package com.tony.rabbitmq.four;


//發布確認模式
//使用時間 比較哪種確認放方式是最好的
//1. 單個確認
//2. 批量確認
//3. 異步批量確認

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;
import com.tony.rabbitmq.uitls.RabbitMqUtils;

import java.io.IOException;
import java.util.BitSet;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class ConfirmMessage {

    private static final String QUEUES_NAME = "tonyTest";

    //批量發消息的個數
    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //單個確認發送(效能低 但是發生異常可以確認是哪個訊息有問題)
//        ConfirmMessage.publishMessateIndividually();//發布1000個單獨確認消息,花了316時間 毫秒

        //批量確認 (效能高 但異常時無法知道哪條數據有問題)
//        ConfirmMessage.publishMessageBatch(); //發布1000個批量確認消息,花了119時間 毫秒


        //異步批量確認 (效能高 在發送完後才 進行[批量確認] 還是有可能丟失訊息必須在 nackCallback 設定確認失敗後儲存或重新處理)
        ConfirmMessage.publishMessageAsync(); //發布1000異步確認消息,花了78時間 毫秒


    }

    public static void publishMessateIndividually() throws Exception{


        Channel channel = RabbitMqUtils.getChannel();
        //隨機生成ID
        String queueName = UUID.randomUUID().toString();

        //生成一個隊列 .queueDeclare參數
        //1.隊列名稱
        //2.隊列裏面的消息是否持久化(磁碟) 默認情況消息存儲在內存中(false)
        //3.該隊列是否只供一個消費者進行消費,是否進行消息共存, true可以多個消費者消費 (默認false)只能一個消費者消費
        //4.是否自動刪除 最後一個消費者斷開連接后 該隊語句是否自動刪除 true 自動刪除 (默認false)不自動刪除
        //5.其他參數

        boolean durable = false; //需要讓 Queue(隊列) 進行持久化 防止mq當機(或重啟)后隊列消失
        // 如果原先已有同名的false隊列會報錯 須先刪除
        channel.queueDeclare(queueName,durable,false,false,null);

        //開啟發布確認
        channel.confirmSelect();

        //開始時間
        long beginTime = System.currentTimeMillis();
        //主要邏輯:批量發消息 單個確認
        for (int i = 1 ; i <= MESSAGE_COUNT ; i++){
            String message = "第" + i + "個消息";

            //發送一個消費 .basicPublish()參數
            //1.發送到哪個交換機
            //2.路由的Key值是哪個 本次是隊列得名稱
            //3.其他參數訊息
            //4.發送消息的消息體 (需要二進制)
            //創建聲明

            //MessageProperties.PERSISTENT_TEXT_PLAIN 設置生產者發送消息為持久化(要求存在磁碟上)
            channel.basicPublish("",queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes("UTF-8"));
            //單個消息就馬上進行確認 同步
            boolean flag = channel.waitForConfirms();
            if(flag) {
                System.out.println("發送消息成功" + message);
            }
        }

        //結束時間
        long endTime = System.currentTimeMillis();
        System.out.println("發布"+MESSAGE_COUNT+"個單獨確認消息,花了"+ (endTime-beginTime) + "時間 毫秒");

    }

    public static void publishMessageBatch() throws IOException, TimeoutException, InterruptedException {

        Channel channel = RabbitMqUtils.getChannel();
        //隨機生成ID
        String queueName = UUID.randomUUID().toString();
        //生成一個隊列 .queueDeclare參數

        boolean durable = false;

        channel.queueDeclare(queueName,durable,false,false,null);
        //開啟發布確認
        channel.confirmSelect();

        //開始時間
        long beginTime = System.currentTimeMillis();

        //批量消息大小(每100比檢查一次)
        int batchSize = 100;
        //主要邏輯:批量發消息 批量確認
        for(int i = 1 ; i <= MESSAGE_COUNT ; i++) {
            String message = "第" + i + "個消息";
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            //取模==0
            if (i%batchSize == 0){
                //發布確認 同步
                channel.waitForConfirms();
                System.out.println("以確認的消息 : " +i );
            }

        }
        //結束時間
        long endTime = System.currentTimeMillis();
        System.out.println("發布"+MESSAGE_COUNT+"個批量確認消息,花了"+ (endTime-beginTime) + "時間 毫秒");
    }

    public static void publishMessageAsync() throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        //開啟確認發布
        channel.confirmSelect();

        //生成對列
//        String queue = UUID.randomUUID().toString();
        String queue = QUEUES_NAME;
        boolean durable = false;
        channel.queueDeclare(queue,durable,false,false,null);


        /*
        * ConcurrentSkipListMap 線程安全有序的一個Has表 是用於高併發情況下
        * 1.將序號與消息進行關連
        * 2.批量刪除條目 只要給到序號
        * 3.支持高併發(多線程)
        * */
        ConcurrentSkipListMap<Long,String> outstandingConfirms =
                new ConcurrentSkipListMap<>();



        /*消息確認成功 回調方法
         * deliveryTag : 消息的標記
         * multiple : 是否批量確認
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple)->{
            //2.刪除以確認的消息
            //multiple 確認機制也是批量的 取決於mq的調度機制
            //生產者這裏兩種都做應對
            if(multiple) {
                //如果是批量刪除 (headMap 會把批量傳來的參數以下的都記錄 例 3(3以內的都以確認)  3以下的都刪除)
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            }else {
                //不是批量刪除
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("以確認的消息 : " + deliveryTag);

        };

        /*消確認失敗 回調方法
        * deliveryTag : 消息的標記
        * multiple : 是否批量確認
        */
        ConfirmCallback nackCallback = (deliveryTag, multiple)->{
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未確認的訊息 : "+message + "  未確認的消息 tsg : " + deliveryTag);

        };


        /*準備消息間聽器
        * params : ackCallback 成功確認消息
        * params : nackCallback 失敗確認消息
        * */
        channel.addConfirmListener(ackCallback,nackCallback); //異步通知

        long beginTime = System.currentTimeMillis();

        //主要邏輯:批量發布 異步確認
        for (int i = 1; i <=MESSAGE_COUNT ; i++){
            String message = "發送消息第"+i+"條";
            //發送消息
            channel.basicPublish("",queue,null,message.getBytes("UTF-8"));
            //1.記錄所有發送的消息 消息的總和
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);

        }
        long endTime = System.currentTimeMillis();
        System.out.println("發布"+MESSAGE_COUNT+"異步確認消息,花了"+ (endTime-beginTime) + "時間 毫秒");




    }

}




















