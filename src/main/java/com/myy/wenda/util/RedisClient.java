package com.myy.wenda.util;

import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClient {

    @Autowired
    private JedisPool jedisPool;

    public void set(String key, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            System.out.println(jedis);
            jedis.set(key, value);
        } finally {
            //返还到连接池
            jedis.close();
        }
    }

    public static void main(String[] args) throws Exception {
        RedisClient redisClient = new RedisClient();

        redisClient.set("a", "123");
    }
}

