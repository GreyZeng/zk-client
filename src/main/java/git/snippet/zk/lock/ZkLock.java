package git.snippet.zk.lock;

import git.snippet.zk.ZookeeperConfig;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static git.snippet.zk.ZookeeperConfig.ADDRESS;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.sort;
import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZkLock implements AsyncCallback.StringCallback, Watcher, AsyncCallback.StatCallback, AsyncCallback.Children2Callback {
    private CountDownLatch latch;
    private ZooKeeper zk;
    private String identify;
    private String lockPath;
    private String pathName;

    public ZkLock() {
        identify = Thread.currentThread().getName();
        lockPath = "/lock";
        latch = new CountDownLatch(1);
        zk = ZookeeperConfig.create(ADDRESS + "/testLock");
    }

    public void lock() {
        try {
            zk.create(lockPath, currentThread().getName().getBytes(UTF_8), OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL, this, currentThread().getName());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            zk.delete(pathName, -1);
            System.out.println(identify + " over work....");
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if (null != name) {
            // 创建成功
            System.out.println(identify + " created " + name);
            pathName = name;
            zk.getChildren("/", false, this, "dasdfas");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        sort(children);
        int i = children.indexOf(pathName.substring(1));
        if (i == 0) {
            // 是第一个，获得锁，可以执行
            System.out.println(identify + " first...");
            try {
                zk.setData("/", identify.getBytes(UTF_8), -1);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        } else {
            zk.exists("/" + children.get(i - 1), this, this, "ddsdf");
        }

    }


    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "sdf");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}
