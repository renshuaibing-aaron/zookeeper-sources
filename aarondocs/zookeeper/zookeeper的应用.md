1.zookeeper在分布式系统中的应用
 分布式协调 ---比如利用zk的watch机制 监听MQ中的消息是否消费
 分布式锁---利用zk实现的分布式锁 性能比redis好
 元数据/配置信息管理--dubbo框架这么实现
 HA的高可靠性---利用临时节点,一旦客户端和zk失去连接 创建的节点会消失  触发watch机制