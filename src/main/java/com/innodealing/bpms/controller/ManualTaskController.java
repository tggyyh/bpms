package com.innodealing.bpms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.domain.WSSynService;
import com.innodealing.bpms.model.SurviveProcess;
import com.innodealing.bpms.model.SynUser;
import com.innodealing.bpms.service.HolidayService;
import com.innodealing.bpms.service.LockService;
import com.innodealing.bpms.service.ProcessService;
import com.innodealing.bpms.service.SynUserService;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.IdentityService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/manual-task")
public class ManualTaskController {
    @Autowired
    private ProcessService processService;
    @Autowired
    IdentityService identityService;

    @Autowired
    private HolidayService holidayService;
    @Autowired
    private LockService lockService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    WSSynService wSSynService;
    @Autowired
    SynUserService synUserService;
    private static final Logger log = LoggerFactory.getLogger(ManualTaskController.class);
    private static final String MANUAL_TASK="manual_task";
    @ResponseBody
    @RequestMapping(value="/",method = RequestMethod.POST)
    public RestResponse<String> startProcess() {
        try {
            Date current = new Date();
            //节假日 直接返回
            if (holidayService.isHoliday(current)) {
                return RestResponse.Success("");
            }
            boolean notLocked = lockService.setLock(MANUAL_TASK,"lock");

            if(!notLocked){
                //其他流程正使用锁
                return RestResponse.Success("-1");
            }
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
            int count = 0;
            count = processService.startProcess(initList);
            log.info("手动启动流程" + count + "笔");
            return RestResponse.Success("");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return RestResponse.Fail("");
        }finally {
            lockService.deleteLock(MANUAL_TASK);
        }
    }
    @ResponseBody
    @RequestMapping(value="/count",method = RequestMethod.POST)
    public RestResponse<Integer> count() {
        int count = 0;
        try {
            Date current = new Date();
            //节假日 直接返回
            if (holidayService.isHoliday(current)) {
                return RestResponse.Success("",count);
            }

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
                return RestResponse.Success("",count);
            }
            count = processService.processCount(initList);
            log.info("today待触发流程" + count + "笔");
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return RestResponse.Success("",count);
    }

    private static final String REMIND_SYS_USER="syn_user";

    @RequestMapping(value="/syn-user",method = RequestMethod.GET)
    public RestResponse<String> synBaseInfo() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String allUsers = wSSynService.synUser();
        log.info(allUsers);
        ObjectMapper mapper = new ObjectMapper();
        List<SynUser> list= mapper.readValue(allUsers,
                new TypeReference<List<SynUser>>(){});
        synUserService.save(list);
        return RestResponse.Success("");
    }
}
