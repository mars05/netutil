package com.github.m5.netutil.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Distributed lock for redis,and support reentrant.
 *
 * @author xiaoyu
 * @see Lock
 */
public class RedisLock implements Lock, Serializable {
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    /**
     * NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     * if it already exist.
     */
    private static final String SET_IF_NOT_EXIST = "NX";
    /**
     * EX|PX, expire time units: EX = seconds; PX = milliseconds
     */
    private static final String SET_WITH_EXPIRE_TIME = "EX";
    private ThreadLocal<String> lockValueHolder = new ThreadLocal<>();
    private Jedis jedis;
    private JedisPool jedisPool;
    private String lockKey;
    private int seconds;


    public RedisLock(Jedis jedis, String lockKey, int expxSeconds) {
        this.jedis = jedis;
        this.lockKey = lockKey;
        this.seconds = expxSeconds;
    }

    public RedisLock(JedisPool jedisPool, String lockKey, int expxSeconds) {
        this.jedisPool = jedisPool;
        this.lockKey = lockKey;
        this.seconds = expxSeconds;
    }

    @Override
    public void lock() {
        try {
            if (lockValueHolder.get() == null) {
                lockValueHolder.set(generateLockValue());
            } else {
                return;
            }
            int lookupIntervalMillis = 50;
            while (!LOCK_SUCCESS.equalsIgnoreCase(getResource().set(lockKey, lockValueHolder.get(), SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, seconds))) {
                try {
                    Thread.sleep(lookupIntervalMillis);
                } catch (InterruptedException e) {
                    throw e;
                }
            }

        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean tryLock() {
        if (lockValueHolder.get() == null) {
            String lockValue = generateLockValue();
            if (LOCK_SUCCESS.equalsIgnoreCase(getResource().set(lockKey, lockValue, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, seconds))) {
                lockValueHolder.set(lockValue);
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (lockValueHolder.get() == null) {
            int lookupIntervalMillis = 50;
            int waitingTimeMillis = 0;
            String lockValue = generateLockValue();
            while (!LOCK_SUCCESS.equalsIgnoreCase(getResource().set(lockKey, lockValue, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, seconds))) {
                try {
                    if (unit.toNanos(time) <= TimeUnit.MILLISECONDS.toNanos(waitingTimeMillis)) {
                        return false;
                    }
                    Thread.sleep(lookupIntervalMillis);
                    waitingTimeMillis += lookupIntervalMillis;
                } catch (InterruptedException e) {
                    throw e;
                }
            }
            lockValueHolder.set(lockValue);
            return true;
        } else {
            return true;
        }
    }

    /**
     * 使用lua脚本，保证原子性，防止错误解除不属于当前的锁
     *
     * @return
     */
    @Override
    public void unlock() {
        if (lockValueHolder.get() == null) {
            throw new IllegalMonitorStateException();
        }
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = getResource().eval(script, Collections.singletonList(lockKey), Collections.singletonList(lockValueHolder.get()));
        lockValueHolder.remove();
        if (!RELEASE_SUCCESS.equals(result)) {
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    private Jedis getResource() {
        if (this.jedis != null) {
            return jedis;
        }
        if (this.jedisPool != null) {
            return jedisPool.getResource();
        }
        throw new NullPointerException();
    }

    private String generateLockValue() {
        return UUID.randomUUID().toString();
    }

}
