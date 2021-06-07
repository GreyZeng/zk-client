package git.snippet.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import static git.snippet.zk.Utils.pending;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Reactive方式实现的zk客户端
 */
public class ReactiveClient {

    private static final ZooKeeper CLIENT = ZookeeperConfig.create();

    public static void main(String[] args) {
        getData();
        getDataWithWatcher();
        getDataAndCallback();
        pending(10000);
    }

    private static void getDataAndCallback() {
        System.out.println("get data and callback");
        String path = "/abc";
        String data = "Hello";
        createOrUpdate(path, data);
        CLIENT.getData(path, false, (rc, path1, ctx, data1, stat) -> {
            //System.out.println(rc);
            //System.out.println(ctx);
            System.out.println("call back get data : " + new String(data1));
            //System.out.println(stat);
        }, "abc");
    }


    private static void getDataWithWatcher() {
        System.out.println("---create and get data with watcher---");
        String path = "/abc";
        String data = "Hello";
        createOrUpdate(path, data);
        Stat stat = new Stat();
        try {
            Stat finalStat = stat;
            byte[] data1 = CLIENT.getData(path, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // System.out.println("get data event: " + event);
                    try {
                        byte[] data2 = CLIENT.getData(path, this, finalStat);
                        System.out.println("get data from event : " + new String(data2));
                    } catch (KeeperException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, stat);
            System.out.println(new String(data1));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        String newData = "World";
        try {
            // 触发回调
            stat = CLIENT.setData(path, newData.getBytes(UTF_8), stat.getVersion());
            stat = CLIENT.setData(path, newData.getBytes(UTF_8), stat.getVersion());
            stat = CLIENT.setData(path, newData.getBytes(UTF_8), stat.getVersion());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void getData() {
        String path = "/abc";
        String data = "Hello";
        createOrUpdate(path, data);
        String result = getData(path);
        System.out.println(result);
        createOrUpdate(path, "world");
        result = getData(path);
        System.out.println(result);
    }


    public static String getData(String path) {
        try {
            return new String(CLIENT.getData(path, false, new Stat()));
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createOrUpdate(String path, String data) {
        try {
            Stat exists = CLIENT.exists(path, false);
            if (null != exists) {
                CLIENT.setData(path, data.getBytes(UTF_8), exists.getVersion());
                return;
            }
            // 创建一个节点
            CLIENT.create(path, data.getBytes(UTF_8), OPEN_ACL_UNSAFE, EPHEMERAL);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
