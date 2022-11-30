# Rabbtmq


![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/amtj2ghbgh.png)


|  名詞   | 描述  |
|  ----  | ----  |
| Queue (隊列)  | 用於存儲消息，消費者直接綁定Queue進行消費消息 |
| Exchange (交換機) | 生產者將消息發送到Exchange，由交換器將消息通過匹配Exchange Type、Binding Key、Routing Key後路由到一個或者多個隊列中。|
| Exchange Type (類型) | Direct、Fanout、Topic、Headers |
| Routing Key  | 生產者發送消息給Exchange會指定一個Routing Key。 |
| Binding Key  | 在綁定Exchange與Queue時會指定一個Binding Key |

# 發佈確認

Channel channel = connection.createChannel();

channel.confirmSelect();


### 1. 單個確認發送( 效能低 但是發生異常可以確認是哪個訊息有問題)

這是一種簡單的確認方式，它是一種同步確認發布的方式，也就是發布一個消息之後只有它 被確認發布，後續的消息才能繼續發布,waitForConfirmsOrDie(long)這個方法只有在消息被確認 的時候才返回，如果在指定時間範圍內這個消息沒有被確認那麼它將拋出異常。

這種確認方式有一個最大的缺點就是:發布速度特別的慢，因為如果沒有確認發布的消息就會 阻塞所有後續消息的發布，這種方式最多提供每秒不超過數百條發布消息的吞吐量。當然對於某 些應用程序來說這可能已經足夠了。

### 2. 批量確認(效能高 但異常時無法知道哪條數據有問題)

與單個等待確認消息相比，先發布一批消息然後一起確認可以極大地 提高吞吐量，當然這種方式的缺點就是:當發生故障導致發布出現問題時，不知道是哪個消息出現 問題了，我們必須將整個批處理保存在內存中，以記錄重要的信息而後重新發布消息。當然這種 方案仍然是同步的，也一樣阻塞消息的發布。

### 3. 異步批量確認(效能高 在發送完後才 進行[批量確認] 還是有可能丟失訊息必須在 nackCallback 設定確認失敗後儲存或重新處理

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-11-29-6.07.23.png)

異步確認雖然編程邏輯比上兩個要複雜，但是性價比最高，無論是可靠性還是效率都沒得說， 他是利用回調函數來達到消息可靠性傳遞的，這个中間件也是通過函數回調來保證是否投遞成功。

demo:tonyRabbitMq/rabbitmqhello/src/main/java/com/tony/rabbitmq/four

演示內容:  生產者 1. 單個確認                                          消費者 1. 手動應答 autoAck = false;
                   waitForConfirms()                                        basicAck() 
                2. 批量確認                                                2. 不公平分發
                   confirmSelect()                                          basicQos()
                3. 異步批量確認 
                   confirmSelect()
                   addConfirmListener(ackCallback,nackCallback);
        
