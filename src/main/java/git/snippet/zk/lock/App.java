package git.snippet.zk.lock;

public class App {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ZkLock lock = new ZkLock();
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " doing work");
                lock.release();

            }).start();
        }
        while (true) {
        }
    }
}
