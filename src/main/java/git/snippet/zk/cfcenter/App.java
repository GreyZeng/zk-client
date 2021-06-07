package git.snippet.zk.cfcenter;

import static git.snippet.zk.Utils.pending;

/**
 * 模拟调用配置中心
 */
public class App {
    public static void main(String[] args) {
        String conf = "/AppConf";
        while (true) {
            String value = ConfigCenter.getConf(conf);
            System.out.println(value);
            pending(1000);
        }
    }
}
