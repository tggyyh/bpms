package com.innodealing.bpms.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innodealing.bpms.domain.WSSynService;
import com.innodealing.bpms.model.SynDept;
import com.innodealing.bpms.service.SynDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
/* 同步天风部门*/
@Component
public class SynDeptTask {
    private static final Logger log = LoggerFactory.getLogger(SynDeptTask.class);
    @Autowired
    WSSynService wSSynService;
    @Autowired
    SynDeptService synDeptService;
    @Scheduled(cron="0 0 5 * * *")
//    @Scheduled(fixedDelay=5000000)
    public void synBaseInfo() throws Exception {
        Long beginTime = System.currentTimeMillis();
        String allDept = wSSynService.synDept();
        log.info(allDept);
        ObjectMapper mapper = new ObjectMapper();
        List<SynDept> list= mapper.readValue(allDept,
                new TypeReference<List<SynDept>>(){});
        synDeptService.save(list);
        log.info("同步数据"+list.size()+"条,耗时:" + (System.currentTimeMillis()-beginTime));
    }
}
