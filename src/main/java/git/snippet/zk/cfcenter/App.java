package git.snippet.zk.cfcenter;

import static git.snippet.zk.Utils.pending;

/**
 * 模拟调用配置中心
 */
public class App {
    public static void main(String[] args) {
        String path = "/AppConf";
        while (true) {
            String conf = new ConfigCenter(path).getConf();
            System.out.println(conf);
            pending(1000);
        }
    }
}
