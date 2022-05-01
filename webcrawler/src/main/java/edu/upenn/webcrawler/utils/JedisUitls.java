package edu.upenn.webcrawler.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

public class JedisUitls {
    private static JedisPool pool = null;
    public static Jedis getJedis() {
        if (pool == null) {
            Properties properties = new Properties();
            try {
                properties.load(JedisUitls.class.getClassLoader().getResourceAsStream("redis.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String ip = (String) properties.get("redis.host");
            int port = Integer.parseInt(properties.get("redis.port").toString()) ;
            String password = (String) properties.get("redis.password");
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(Integer.parseInt(properties.get("redis.maxTotal").toString()));
            jedisPoolConfig.setMaxIdle(Integer.parseInt(properties.get("redis.maxIdle").toString()));
            jedisPoolConfig.setMaxWaitMillis(Long.parseLong(properties.get("redis.maxWaitMillis").toString()));
            if (password != null && !"".equals(password)) {
                // redis existed password
                pool = new JedisPool(jedisPoolConfig, ip, port, 10000, password);
            } else {
                // redis none password
                pool = new JedisPool(jedisPoolConfig, ip, port, 10000);
            }
            return pool.getResource();
        }else{
            return pool.getResource();
        }
    }
}
