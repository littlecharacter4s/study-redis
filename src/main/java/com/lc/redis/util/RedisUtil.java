package com.lc.redis.util;

import com.lc.redis.base.Constants;
import redis.clients.jedis.*;

import java.util.*;

public final class RedisUtil {
    private static final String SUCCESS = "OK";
    private static JedisCluster jedisCluster;

    private RedisUtil() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.parseInt(PropertyUtil.instance().getProperty(Constants.REDIS_MAX_TOTAL)));
        jedisPoolConfig.setMaxIdle(Integer.parseInt(PropertyUtil.instance().getProperty(Constants.REDIS_MAX_IDLE)));
        jedisPoolConfig.setMinIdle(Integer.parseInt(PropertyUtil.instance().getProperty(Constants.REDIS_MIN_IDLE)));
        jedisPoolConfig.setMaxWaitMillis(Long.parseLong(PropertyUtil.instance().getProperty(Constants.REDIS_MAX_WAIT_MILLIS)));
        jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(PropertyUtil.instance().getProperty(Constants.REDIS_TEST_ON_BORROW)));
        jedisPoolConfig.setTestWhileIdle(Boolean.parseBoolean(PropertyUtil.instance().getProperty(Constants.REDIS_TEST_WHILE_IDLE)));
        jedisPoolConfig.setTestOnReturn(Boolean.parseBoolean(PropertyUtil.instance().getProperty(Constants.REDIS_TEST_ON_RETURN)));

        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort(PropertyUtil.instance().getProperty(Constants.REDIS_HOST), Integer.parseInt(PropertyUtil.instance().getProperty(Constants.REDIS_PORT1))));
        jedisClusterNode.add(new HostAndPort(PropertyUtil.instance().getProperty(Constants.REDIS_HOST), Integer.parseInt(PropertyUtil.instance().getProperty(Constants.REDIS_PORT2))));
        jedisClusterNode.add(new HostAndPort(PropertyUtil.instance().getProperty(Constants.REDIS_HOST), Integer.parseInt(PropertyUtil.instance().getProperty(Constants.REDIS_PORT3))));

        jedisCluster = new JedisCluster(jedisClusterNode, 5000, 5000, 5, jedisPoolConfig);
    }

    private static class RedisUtilInner {
        private static final RedisUtil redisUtil = new RedisUtil();

        private RedisUtilInner() {
        }
    }

    public static RedisUtil instance() {
        return RedisUtilInner.redisUtil;
    }

    public boolean set(String key, String value) {
        try {
            String result = jedisCluster.set(key, value);
            return Objects.equals(SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String set(String key) {
        try {
            return jedisCluster.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> keys() {
        try {
            Set<String> keySet = new TreeSet<>();
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            for(String key : clusterNodes.keySet()){
                JedisPool jedisPool = clusterNodes.get(key);
                System.out.println("[INFO ]" + key + "=" + jedisPool);
                try {
                    Jedis jedis = jedisPool.getResource();
                    keySet.addAll(jedis.keys("*"));
                } catch (Exception e) {
                    System.out.println("[ERROR]" + key + "=" + jedisPool);
                }
            }
            return keySet;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }
}
