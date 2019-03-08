package lock;

import com.github.m5.netutil.util.RedisConfig;
import com.github.m5.netutil.util.RedisLock;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.locks.Lock;

/**
 * @author xiaoyu
 */
public class RedisLockTest {

    public static void main(String[] args) throws Exception {
        JedisPool jedisPool = RedisConfig.loadByResource().newJedisPool();
        Lock lock = new RedisLock(jedisPool, "LOCK_KEY_TEST", 5);
        new Thread(() -> {
            lock.lock();
            System.out.println("1111获得锁");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();
        new Thread(() -> {
            lock.lock();
            System.out.println("222获得锁");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();
        new Thread(() -> {
            lock.lock();
            System.out.println("333获得锁");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();
        new Thread(() -> {
            lock.lock();
            System.out.println("4444获得锁");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();

    }

}
