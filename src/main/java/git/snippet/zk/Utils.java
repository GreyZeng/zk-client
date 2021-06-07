package git.snippet.zk;

public class Utils {
    public static void pending(long sec) {
        try {
            Thread.sleep(sec);
            ZookeeperConfig.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
