package com.myy.wenda.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {
    private String host = "127.0.0.1";
    private int port = 6379;
    private int database = 10;
    private int timeout = 10;
    private String password = "my231921";
    private int poolMaxTotal = 1000;
    private int poolMaxIdle = 500;
    private int poolMaxWait = 500;

    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig poolconfig = new JedisPoolConfig();
        poolconfig.setMaxIdle(poolMaxIdle);
        poolconfig.setMaxTotal(poolMaxTotal);
        poolconfig.setMaxWaitMillis(poolMaxWait*1000);
        // JedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database)
        JedisPool jp = new JedisPool(poolconfig,host,port,
                timeout*1000,password,database);
        return jp;
    }


}
