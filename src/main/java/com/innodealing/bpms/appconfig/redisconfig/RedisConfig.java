package com.innodealing.bpms.appconfig.redisconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;
    @Primary
    @Bean(name = "redisTemplate")
    public StringRedisTemplate createStringRedisTemplate() {
        StringRedisTemplate stringTemplate = new StringRedisTemplate();
        stringTemplate.setConnectionFactory(connectionFactory());
        stringTemplate.afterPropertiesSet();
        return stringTemplate;
    }
    @Bean(name = "redisTemplate1")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory());
        RedisSerializer stringSerializer = new StringRedisSerializer();
        //RedisSerializer jackson2Serializer = new GenericJackson2JsonRedisSerializer();

        redisTemplate.setKeySerializer(stringSerializer);
        //redisTemplate.setValueSerializer(jackson2Serializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        //redisTemplate.setHashValueSerializer(jackson2Serializer);
        return redisTemplate;
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory conn = new JedisConnectionFactory();
        conn.setHostName(host);
        conn.setPassword(password);
        conn.setPort(port);
        conn.setDatabase(database);
        return conn;
    }
}
