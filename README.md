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


# 消息應答

Channel.basicAck();



### 1. 自動應答 (可能丟失訊息,適合在消費者可以高效並以某種速率能夠處理這些消息的情況下使用)

消息發送后立即被認為已經傳送成功，這種模式需要在高吞吐量和數據傳輸安全性方面做權 衡,因為這種模式如果消息在接收到之前，消費者那邊出現連接或者 channel 關閉，那麼消息就丟 失了,當然另一方面這種模式消費者那邊可以傳遞過載的消息，沒有對傳遞的消息數量進行限制， 當然這樣有可能使得消費者這邊由於接收太多還來不及處理的消息，導致這些消息的積壓，最終 使得內存耗盡，最終這些消費者線程被操作系統殺死，所以這種模式僅適用在消費者可以高效,並以某種速率能夠處理這些消息的情況下使用。

### 2. 手動應答 (確保消息步丟失,可以重新安排自隊列)

消費者處理完業務邏輯，手動返回ack（通知）告訴隊列處理完了，隊列進而刪除消息

### 3. 消息自東重新入隊

如果消費者由於某些原因失去連接(其通道已關閉，連接已關閉或 TCP 連接丟失)，導致消息 未發送 ACK 確認，RabbitMQ 將了解到消息未完全處理，並將對其重新排隊。如果此時其他消費者 可以處理，它將很快將其重新分發給另一個消費者。這樣，即使某個消費者偶爾死亡，也可以確 保不會丟失任何消息。

### 4. 不公平分發 (能者多勞,可以讓先處理完的繼續下個等待中得訊息,  默認為輪序分發 )

RabbitMQ 分發消息採用的輪訓分發，但是在某種場景下這種策略並不是 很好，比方說有兩個消費者在處理任務，其中有個消費者 1 處理任務的速度非常快，而另外一個消費者 2 處理速度卻很慢，這個時候我們還是採用輪訓分發的化就會到這處理速度快的這個消費者很大一部分時間 處於空閑狀態，而處理慢的那個消費者一直在幹活，這種分配方式在這種情況下其實就不太好，但是 RabbitMQ 並不知道這種情況它依然很公平的進行分發。

### 5. 預取值 ()

設置預取值可以限制未被確認的消息個數，一旦消費者中未被確認的消息數量達到設置的預取值，服務端將不再向此消費者發送消息，除非至少有一個未被確認的消息被確認。設置預取值本質上是一種對消費者進行流控的方法。  
預取值設置太小可能會損害性能，RabbitMQ會一直在等待獲得發送消息的權限。  
預取值設置太大可能會導致從隊列中取出大量消息傳遞給一個消費者，而使其他消費者處於空閑狀態。  




### 演示內容:demo:tonyRabbitMq/rabbitmqhello/src/main/java/com/tony/rabbitmq/four  
#### 生產者 
1. 單個確認  
waitForConfirms()                                     
2. 批量確認                                                
confirmSelect()                                        
3. 異步批量確認 
confirmSelect()  
addConfirmListener(ackCallback,nackCallback);  
#### 消費者    
1. 手動應答 autoAck = false;        
basicAck()        
2. 不公平分發
basicQos()
