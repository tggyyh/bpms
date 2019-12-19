package com.innodealing.bpms.task;

import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.model.ProcessInfo;
import com.innodealing.bpms.service.BondService;
import java.util.List;

import com.innodealing.bpms.service.ProcessInfoService;
import com.innodealing.bpms.service.ProcessService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;

@Component
public class BondExpireTask {
    @Autowired
    private BondService bondService;
    @Autowired
    private ProcessInfoService processInfoService;
    @Autowired
    private ProcessService processService;

    private static final Logger log = LoggerFactory.getLogger(BondExpireTask.class);

    @Scheduled(cron="0 0 1 * * *")
//    @Scheduled(fixedDelay=5000000)
    public void execBondExpire() throws ParseException {
        Long beginTime = System.currentTimeMillis();
        int expireCount = 0;
        try {
            List<Bond> bondList = bondService.findBondExpire();
            if(!CollectionUtils.isEmpty(bondList)){
                for(Bond bond:bondList){
                    String code =  bond.getCode();
                    bondService.updateBondExpireByCode(code);
                    List<ProcessInfo> list = processInfoService.findByBondCode(code);
                    if(!CollectionUtils.isEmpty(list)){
                        for(ProcessInfo pi:list){
                            processService.cancelPreocess(pi.getProcessId());
                        }
                    }

                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        log.info("项目到期耗时:" + (System.currentTimeMillis()-beginTime));
    }
}
