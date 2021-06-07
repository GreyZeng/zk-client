package git.snippet.zk.cfcenter;

import git.snippet.zk.ZookeeperConfig;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ConfigCenter implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ZooKeeper zk = ZookeeperConfig.create();
    private final String conf;
    private String value;

    public ConfigCenter(String conf) {
        this.conf = conf;
    }

    public String getConf() {
        aWait();
        return value;
    }

    public void aWait() {
        zk.exists(conf, this, this, "dasdfa");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void process(WatchedEvent event) {

        Event.EventType type = event.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                System.out.println("created");
                zk.getData(conf, this, this, "node created");
                latch.countDown();
                break;
            case NodeDeleted:
                break;
            case NodeDataChanged:
                System.out.println("changed");
                zk.getData(conf, this, this, "node changed");
                latch.countDown();
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            zk.getData(conf, this, this, "getData");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            this.value = new String(data);
            latch.countDown();
        }
    }
}
