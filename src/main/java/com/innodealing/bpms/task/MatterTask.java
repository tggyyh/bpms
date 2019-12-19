package com.innodealing.bpms.task;

import com.innodealing.bpms.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class MatterTask {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private HolidayService holidayService;
    public void  handle(String matter){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Long result = redisTemplate.opsForSet().add(matter,formatter.format(new Date()));

        if(0 == result){
            return;
        }
        Date current = new Date();
        //节假日 直接返回
        if(holidayService.isHoliday(current)){
            return;
        };
        execute();
    };
    public abstract void execute();
}
