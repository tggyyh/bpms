package com.innodealing.bpms.task.MatterTimerTask;

import com.innodealing.bpms.model.SurviveProcess;
import com.innodealing.bpms.service.HolidayService;
import com.innodealing.bpms.service.LockService;
import com.innodealing.bpms.service.ProcessService;
import com.innodealing.commons.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component
public class TodayMatterTask {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private LockService lockService;
    private static final Logger log = LoggerFactory.getLogger(TodayMatterTask.class);
    private static final String TODAY_REMIND_MATTER="today_matter";
    private static final String MANUAL_TASK="manual_task";
    @Scheduled(cron="0 0 9-23/1 * * ?")
//        @Scheduled(fixedDelay=50000000)
    public void  handle(){
        Long beginTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
        Long result = redisTemplate.opsForSet().add(TODAY_REMIND_MATTER, formatter.format(new Date()));
        if (0 == result) {
            return;
        }
        Date current = new Date();
        //节假日 直接返回
        if (holidayService.isHoliday(current)) {
            return;
        }
        boolean notLocked = lockService.setLock(MANUAL_TASK,"lock");
        if(!notLocked){
            return;
        }
        try {
            List<SurviveProcess> initList = new ArrayList();
            List<SurviveProcess> companyList = processService.findCompanyProcess();
            List<SurviveProcess> bondList = processService.findBondProcess();
            List<SurviveProcess> customCompanyList = processService.findCustomCompanyProcess();
            List<SurviveProcess> customBondList = processService.findCustomBondProcess();
            List<SurviveProcess> rightLineList = processService.findRightLineProcess();
            initList.addAll(companyList);
            initList.addAll(bondList);
            initList.addAll(customCompanyList);
            initList.addAll(customBondList);
            initList.addAll(rightLineList);
            if (null == initList || initList.size() == 0) {
                return;
            }
            int count = 0;
            count = processService.startProcess(initList);
            log.info("today启动流程" + count + "笔,耗时:" + (System.currentTimeMillis() - beginTime));
        }catch(Exception e) {
            log.error(e.getMessage(),e);
        }finally {
            lockService.deleteLock(MANUAL_TASK);
        }
    }
}
