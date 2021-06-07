package git.snippet.zk.cfcenter;

import git.snippet.zk.ZookeeperConfig;
import org.apache.zookeeper.ZooKeeper;

/**
 * 模拟调用配置中心
 */
public class App {
    public static void main(String[] args) {
        ZooKeeper zooKeeper = ZookeeperConfig.create();
        //


    }
}
