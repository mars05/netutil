package com.github.m5.netutil.util;

import io.netty.util.internal.StringUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.HashMap;

/**
 * @author xiaoyu
 */
public class RedisConfig {
    private String host;
    private Integer port;
    private String password;
    private Integer minIdle;
    private Integer maxIdle;
    private Integer maxTotal;
    private Integer maxWaitMillis;
    private Integer minEvictableIdleTimeMillis;
    private Integer timeBetweenEvictionRunsMillis;
    private Boolean testWhileIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;

    public static RedisConfig loadByResource() {
        RedisConfig redisConfig = new RedisConfig();
        Object yaml = ConfigUtils.loadYaml("redis.yaml");
        if (null == yaml) {
            Object yml = ConfigUtils.loadYaml("redis.yml");
            if (null == yml) {
                throw new RuntimeException("Redis config Resource file doesn't exist");
            }
            yaml = yml;
        }
        if (!(yaml instanceof HashMap)) {
            throw new RuntimeException("redis yaml file format error");
        }
        HashMap<String, Object> ymlObj = (HashMap<String, Object>) yaml;
        Object value;
        if ((value = ymlObj.get("host")) != null) {
            redisConfig.host = String.valueOf(value);
        }
        if ((value = ymlObj.getOrDefault("port", Protocol.DEFAULT_PORT)) != null) {
            redisConfig.port = (Integer) value;
        }
        if ((value = ymlObj.get("password")) != null) {
            redisConfig.password = String.valueOf(value);
        }
        if ((value = ymlObj.get("minIdle")) != null) {
            redisConfig.minIdle = (Integer) value;
        }
        if ((value = ymlObj.get("maxIdle")) != null) {
            redisConfig.maxIdle = (Integer) value;
        }
        if ((value = ymlObj.get("maxTotal")) != null) {
            redisConfig.maxTotal = (Integer) value;
        }
        if ((value = ymlObj.get("minEvictableIdleTimeMillis")) != null) {
            redisConfig.minEvictableIdleTimeMillis = (Integer) value;
        }
        if ((value = ymlObj.get("timeBetweenEvictionRunsMillis")) != null) {
            redisConfig.timeBetweenEvictionRunsMillis = (Integer) value;
        }
        if ((value = ymlObj.get("maxWaitMillis")) != null) {
            redisConfig.maxWaitMillis = (Integer) value;
        }
        if ((value = ymlObj.get("testWhileIdle")) != null) {
            redisConfig.testWhileIdle = (Boolean) value;
        }
        if ((value = ymlObj.get("testOnBorrow")) != null) {
            redisConfig.testOnBorrow = (Boolean) value;
        }
        if ((value = ymlObj.get("testOnReturn")) != null) {
            redisConfig.testOnReturn = (Boolean) value;
        }
        return redisConfig;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Integer maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public JedisPool newJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        initPool(jedisPoolConfig);
        JedisPool jedisPool;
        if (StringUtil.isNullOrEmpty(this.getPassword())) {
            jedisPool = new JedisPool(jedisPoolConfig, this.getHost(), this.getPort(), this.getMaxWaitMillis());
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, this.getHost(), this.getPort(), this.getMaxWaitMillis(), this.getPassword());
        }
        return jedisPool;
    }

    private void initPool(JedisPoolConfig jedisPoolConfig) {
        if (this.getMinIdle() != null) {
            jedisPoolConfig.setMinIdle(this.getMinIdle());
        }
        if (this.getMaxIdle() != null) {
            jedisPoolConfig.setMaxIdle(this.getMaxIdle());
        }
        if (this.getMinEvictableIdleTimeMillis() != null) {
            jedisPoolConfig.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        }
        if (this.getMaxTotal() != null) {
            jedisPoolConfig.setMaxTotal(this.getMaxTotal());
        }
        if (this.getMaxWaitMillis() != null) {
            jedisPoolConfig.setMaxWaitMillis(this.getMaxWaitMillis());
        }
        if (this.getTestWhileIdle() != null) {
            jedisPoolConfig.setTestWhileIdle(this.getTestWhileIdle());
        }
        if (this.getTestOnBorrow() != null) {
            jedisPoolConfig.setTestOnBorrow(this.getTestOnBorrow());
        }
        if (this.getTestOnReturn() != null) {
            jedisPoolConfig.setTestOnReturn(this.getTestOnReturn());
        }
        if (this.getTimeBetweenEvictionRunsMillis() != null) {
            jedisPoolConfig.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        }
    }
}
