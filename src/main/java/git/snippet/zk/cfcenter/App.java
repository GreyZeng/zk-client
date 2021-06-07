package git.snippet.zk.cfcenter;

import static git.snippet.zk.Utils.pending;

/**
 * 模拟调用配置中心
 */
public class App {
    public static void main(String[] args) {
        String path = "/AppConf";

        while (true) {
            ConfigCenter configCenter = new ConfigCenter(path);
            String conf = configCenter.getConf();
            if (null != conf && !conf.trim().isEmpty()) {
                System.out.println(conf);
            } else {
                configCenter.getConf();
            }
            pending(1000);
        }
    }
}
