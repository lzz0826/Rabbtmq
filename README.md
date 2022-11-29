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


###1. 單個確認發送
###2. 批量確認
###3. 異步批量確認


