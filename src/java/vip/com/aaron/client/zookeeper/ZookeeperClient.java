package com.aaron.client.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;


public class ZookeeperClient {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        // 默认的watch
        ZooKeeper client = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", 10000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("默认的watch:" + event.getType());
            }
        }, false);

        client.create("/aaron", "lb".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //        client.create("/aaron", "lb".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        //        client.create("/luban2", "lb".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        //        client.create("/aaron", "lb".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // watcher的一致性
        client.getData("/aaron", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("get绑定的watch");
            }
        }, null);


        System.out.println(new String(client.getData("/aaron", true, null)) + "==========");

        // stat怎么用？stat代表的是节点信息，如果想获取出节点信息就可以
        //        Stat stat = new Stat();
        //        client.getData("/aaron", false, stat);
        //        System.out.println(stat.getCzxid());

        //        client.setData("/aaron", "2".getBytes(), stat.getVersion());
        //        client.setData("/aaron", "2".getBytes(), -1);

        //        List<String> children = client.getChildren("/aaron", false);
        //        System.out.println(children);

        //        Stat stat = client.exists("/luban1", false);
        //        System.out.println(stat);

        // 异步回调
        //        client.getData("/aaron", false, new AsyncCallback.DataCallback() {
        //            @Override
        //            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        //                System.out.println("DataCallback");
        //            }
        //        }, null);
        //
        //        client.getChildren("/aaron", false, new AsyncCallback.ChildrenCallback() {
        //            @Override
        //            public void processResult(int rc, String path, Object ctx, List<String> children) {
        //                System.out.println("ChildrenCallback");
        //            }
        //        }, null);


        System.in.read();
    }
}
