# Rabbtmq


![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/amtj2ghbgh.png)


|  名詞   | 描述  |
|  ----  | ----  |
| Queue (隊列)  | 用於存儲消息，消費者直接綁定Queue進行消費消息 |
| Exchange (交換機) | 生產者將消息發送到Exchange，由交換器將消息通過匹配Exchange Type、Binding Key、Routing Key後路由到一個或者多個隊列中。|
| Exchange Type (交換機類型) | Direct、Fanout、Topic、Headers |q
| Routing Key  | 生產者發送消息給Exchange會指定一個Routing Key。 |
| Binding Key  | 在綁定Exchange與Queue時會指定一個Binding Key |

補充 : Routing Key ans Binding Key   

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-11-30%203.40.43.png)

Bindings : 綁定是交換機和隊列之間的橋樑關係。也可以這麼理解: 隊列只對它綁定的交換機的消息感興趣。綁定用參數:routingKey 來表示也可稱該參數為 binding key， 創建綁定我們用代碼:channel.queueBind(queueName, EXCHANGE_NAME, "routingKey");綁定之後的 意義由其交換類型決定。

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


# 消息應答(無設定交換機的情況下)

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/022-11-30%2010.34.58.png)

Channel.basicAck();



### 1. 自動應答 (可能丟失訊息,適合在消費者可以高效並以某種速率能夠處理這些消息的情況下使用)

消息發送后立即被認為已經傳送成功，這種模式需要在高吞吐量和數據傳輸安全性方面做權 衡,因為這種模式如果消息在接收到之前，消費者那邊出現連接或者 channel 關閉，那麼消息就丟 失了,當然另一方面這種模式消費者那邊可以傳遞過載的消息，沒有對傳遞的消息數量進行限制， 當然這樣有可能使得消費者這邊由於接收太多還來不及處理的消息，導致這些消息的積壓，最終 使得內存耗盡，最終這些消費者線程被操作系統殺死，所以這種模式僅適用在消費者可以高效,並以某種速率能夠處理這些消息的情況下使用。

### 2. 手動應答 (確保消息不丟失,可以重新安排自隊列)

消費者處理完業務邏輯，手動返回ack（通知）告訴隊列處理完了，隊列進而刪除消息

### 3. 消息自動重新入隊

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

# 交換機 exchange

創建交換機 : channel.exchangeDeclare(exchange , type)  

綁定交換機與隊列: channel.queueBind(queue, exchange, routingKey)  


### 1 Fanout 扇出模式(發佈訂閱 需要同時發給多個消費者處理可以透過交換機Fanout模式發給多個信道) routingKey 設定同一個  

### 演示內容:demo:tonyRabbitMq/rabbitmqhello/src/main/java/com/tony/rabbitmq/five 

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/%202022-11-301.16.32.png)

正如從名稱中猜到的那樣，它是將接收到的所有消息廣播到它知道的 所有隊列中。  

創建臨時隊列 channel.queueDeclare().getQueue() 當消費者斷開與隊列的連接後 列隊自動刪除  
綁定交換機與隊列 queueBind(queue,exchange, routingKey)

### 2 Direct (路由模式)

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-11-30%203.18.47.png)


Fanout 這種交換類型並不能給我們帶來很大的靈活性-它只能進行無意識的 廣播，在這裏我們將使用 direct 這種類型來進行替換，這種類型的工作方式是，消息只去到它綁定的 routingKey 隊列中去。   
例如我們希 望將日誌消息寫入磁盤的程序僅接收嚴重錯誤(errros)，而不存儲哪些警告(warning)或信息(info)日誌 消息避免浪費磁盤空間。  

### 演示內容:demo:tonyRabbitMq/rabbitmqhello/src/main/java/com/tony/rabbitmq/six 

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-11-30%203.19.00.png)



### 3 Topic (主題模式)

發送到類型是 topic 交換機的消息的 routing_key 不能隨意寫，必須滿足一定的要求，它必須是一個單 詞列表，以點號分隔開。這些單詞可以是任意單詞，比如說:"stock.usd.nyse", "nyse.vmw", "quick.orange.rabbit".這種類型的。當然這個單詞列表最多不能超過 255 個字節。  

在這個規則列表中，其中有兩個替換符是大家需要注意的  
(*星號)可以代替一個單詞  
(#井號)可以替代零個或多個單詞   

Topic (主題模式)中是包含 Fanout (扇出模式.發佈訂閱) 和 Direct (路由模式) 當隊列綁定關係是下列這種情況時需要引起注意。   
當一個隊列綁定鍵是#,那麼這個隊列將接收所有數據，就有點像 fanout了  
如果隊列綁定鍵當中沒有#和*出現，那麼該隊列綁定類型就是 direct 了

下圖表示 :   
Q1-->綁定的是  
中間帶 orange 帶 3 個單詞的字符串(*.orange.*)  
Q2-->綁定的是  
最後一個單詞是 rabbit 的 3 個單詞(*.*.rabbit)  
第一個單詞是 lazy 的多個單詞(lazy.#)    

![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-12-01%2010.16.44.png)

### 演示內容:demo:tonyRabbitMq/rabbitmqhello/src/main/java/com/tony/rabbitmq/seven   

quick.orange.rabbit : 被隊列 Q1Q2 接收到  
lazy.orange.elephant : 被隊列 Q1Q2 接收到  
quick.orange.fox : 被隊列 Q1 接收到   
lazy.brown.fox : 被隊列 Q2 接收到   
lazy.pink.rabbit : 雖然滿足兩個綁定但只被隊列 Q2 接收一次   
quick.brown.fox : 不匹配任何綁定不會被任何隊列接收到會被丟棄  
quick.orange.male.rabbit : 是四個單詞不匹配任何綁定會被丟棄   
lazy.orange.male.rabbit : 是四個單詞但匹配   


# 死信隊列

### 有三種情況會成為死信  

### 1.消息TTL過期(推薦設定在生產者中 可以對應不同的需要)   

在生產者中設定(單位ms) : new AMQP.BasicProperties().builder().expiration("10000").build();  
在消費者中設定 : arguments.put("x-message-ttl",10000);
![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-12-03%203.07.40.png)


### 2.隊列達到最大長度   

在消費者中設定 : arguments.put("x-max-length",6);
![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-12-03%203.17.00.png)


### 3.消息被拒絕(需要手動答應)   

 在消費者中設定 : channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-12-03%203.18.59.png)
![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-12-03%203.15.51.png)


### 演示內容:demo:tonyRabbitMq/rabbitmqhello/src/main/java/com/tony/rabbitmq/eight    
![image](https://github.com/lzz0826/Rabbtmq/blob/main/jpg/2022-12-03%202.55.45.png)



# 延遲隊列

延時隊列,隊列內部是有序的，最重要的特性就體現在它的延時屬性上，延時隊列中的元素是希望在指定時間到了以後或之前取出和處理，簡單來說，延時隊列就是用來存放需要在指定時間被處理的
元素的隊列。

###延遲隊列使用場景:  
1.訂單在十分鐘之內未支付則自動取消. 
2.新創建的店鋪，如果在十天內都沒有上傳過商品，則自動發送消息提醒。  
3.用戶註冊成功后，如果三天內沒有登陸則進行短信提醒。  
4.用戶發起退款，如果三天內沒有得到處理則通知相關運營人員。  
5.預定會議后，需要在預定的時間點前十分鐘通知各個與會人員參加會議. 

![image]()

###RabbitMQ 中的 TTL  

TTL 是 RabbitMQ 中一個消息或者隊列的屬性，表明一條消息或者該隊列中的所有消息的最大存活時間，單位是毫秒。換句話說，如果一條消息設置了 TTL 屬性或者進入了設置 TTL 屬性的隊列，那麼這
條消息如果在 TTL 設置的時間內沒有被消費，則會成為"死信"。如果同時配置了隊列的 TTL 和消息的TTL，那麼較小的那個值將會被使用，有兩種方式設置 TTL。   

###隊列設置 TTL:   

arguments.put("x-message-ttl",10000);
return QueueBuilder.durable(QA_QUEUE).withArguments(arguments).build();

###消息設置(推薦使用生產者配置 可以使用一個信道和交換機靈活配置時間) TTL   

MessagePostProcessor 設定參數    

      //MessagePostProcessor 設定參數    
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {    
            @Override   
            public Message postProcessMessage(Message message) throws AmqpException {   
                //設置時間   
                message.getMessageProperties().setExpiration(ttlTime);   
                return message;   
            }   
        };  
rabbitTemplate.convertAndSend("X","XC",message,messagePostProcessor);  

###兩者區別:   
如果設置了隊列的 TTL 屬性，那麼一旦消息過期，就會被隊列丟棄(如果配置了死信隊列被丟到死信隊列中)，而第二種方式，消息即使過期，也不一定會被馬上丟棄，因為消息是否過期是在即將投遞到消費者之前判定的，如果當前隊列有嚴重的消息積壓情況，則已過期的消息也許還能存活較長時間；另外，還需要注意的一點是，如果不設置 TTL，表示消息永遠不會過期，如果將 TTL 設置為 0，則表示除非此時可以直接投遞該消息到消費者，否則該消息將會被丟棄。


###隊列 TTL 實例:     

![image]()   

發起請求:  
http://localhost:8080/ttl/sendExpirationMsg/你好 1/20000  
http://localhost:8080/ttl/sendExpirationMsg/你好 2/2000   
    
預計情況 你好 2 會先被接收到 , 但是結果是 你好 1  你好 2 同時個收到。  

![image]()  

最開始的時候，就介紹過如果使用在消息屬性上設置 TTL 的方式，消息可能並不會按時“死亡“，因為 RabbitMQ 只會檢查第一個消息是否過期，如果過期則丟到死信隊列，如果第一個消息的延時時長很長，而第二個消息的延時時長很短，第二個消息並不會優先得到執行(RabbitMQ 是依照順序排隊的)。







