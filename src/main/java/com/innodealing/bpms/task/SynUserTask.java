package com.innodealing.bpms.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innodealing.bpms.domain.WSSynService;
import com.innodealing.bpms.model.SynUser;
import com.innodealing.bpms.service.SynUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/* 同步天风用户*/
@Component
public class SynUserTask {
    @Autowired
    WSSynService wSSynService;
    @Autowired
    SynUserService synUserService;
    private static final Logger log = LoggerFactory.getLogger(SynUserTask.class);
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String REMIND_SYS_USER="syn_user";
    @Scheduled(cron="0 10 6 * * *")
//    @Scheduled(fixedDelay=5000000)
    public void synBaseInfo() throws Exception {
        Long beginTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Long result = redisTemplate.opsForSet().add(REMIND_SYS_USER,formatter.format(new Date()));
        if(0 == result){
            return;
        }
        String allUsers = wSSynService.synUser();
        log.info(allUsers);
        ObjectMapper mapper = new ObjectMapper();
        List<SynUser> list= mapper.readValue(allUsers,
                new TypeReference<List<SynUser>>(){});
        synUserService.save(list);
        log.info("同步数据"+list.size()+"条,耗时:" + (System.currentTimeMillis()-beginTime));
    }
}
