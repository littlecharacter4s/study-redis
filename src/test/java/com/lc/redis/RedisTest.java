package com.lc.redis;

import com.lc.redis.util.RedisUtil;
import org.junit.Test;

public class RedisTest {

    @Test
    public void testRedis() {

    }

    @Test
    public void test() {
        System.out.println(RedisUtil.instance().keys());
    }
}
