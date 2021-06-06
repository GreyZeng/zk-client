package git.snippet.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperConfig {
    private static final String ADDRESS = "192.168.205.145:2181,192.168.205.146:2181,192.168.205.147:2181,192.168.205.148:2181";
    private static ZooKeeper zk;
    static CountDownLatch latch;

    public static ZooKeeper create() {
        latch = new CountDownLatch(1);
        try {
            zk = new ZooKeeper(ADDRESS, 3000, new DefaultWatch());
            latch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return zk;
    }

    public static void close() {
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class DefaultWatch implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                latch.countDown();
            }
        }
    }
}
