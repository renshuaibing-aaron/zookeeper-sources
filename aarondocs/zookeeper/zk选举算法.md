1.zookeep 选举算法协议是zab协议 是一种少数服从多数的协议  在3.4.0之后 zk里面只保留一种FastLeaderElection算法
 首先先备用的知识是 协议中用到的一些概念：
 SID  服务器ID 在集群的配置文件里面配置
 ZXID 事务ID 用来标识服务器的一次状态变更 集群中的每台机器上的ZXID可能都不一样
 
 Vote 投票选举 当集群中发现自己无法检测到leader时 开始尝试进行投票
 Quorum  过半机器数 
 
2.选举的过程

  投票的消息里面包含机器ID和事务ID
  广播消息 先比较事务ID 选择事务比较大的ID
  事务ID相同的比较机器ID 选择机器ID比较大的
  如果发现自己的值大于别的机器广播的消息 自己的投票不做变更
  进行新的投票
  每轮结束进行统计 发现一台服务器收到超过半数的相同投票 那么这个服务器的SID为leader


3.一个常见的误区是  为什么需要对zk的集群设置为奇数 
  原因是 是因为奇数和奇数+1的容灾能力是一样的
  
  
4.在Zookeeper中怎么保证事务ID的唯一  