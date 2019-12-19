package com.innodealing.bpms.service;

import com.innodealing.bpms.controller.ManualTaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class LockService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(LockService.class);
    //加锁
    public Boolean setLock(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return success;
                }
            });
        } catch (Exception e) {
            log.error("setNX redis error, key : {}", key);
        }
        return obj != null ? (Boolean) obj : false;
    }
    //解锁
    public void deleteLock(final String key) {
        redisTemplate.delete(key);
    }
}
