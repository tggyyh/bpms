package com.innodealing.bpms.task.MatterTimerTask;

import com.innodealing.bpms.model.SurviveProcess;
import com.innodealing.bpms.service.ExceptionProcessService;
import com.innodealing.bpms.service.ProcessService;
import com.innodealing.bpms.task.MatterTask;
import com.innodealing.bpms.task.rule.RuleService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomRightLineMatterTask extends MatterTask{
    @Autowired
    private ProcessService processService;
    private static final String REMIND_CUSTOM_RIGHT_LINE="right_line";
    private static final Logger log = LoggerFactory.getLogger(CustomRightLineMatterTask.class);
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ExceptionProcessService exceptionProcessService;
    @Scheduled(cron="0 0 8 * * *")
//    @Scheduled(fixedDelay=50000000)
    public void handle() {
        super.handle(REMIND_CUSTOM_RIGHT_LINE);
    }
    public void execute(){
        Long beginTime = System.currentTimeMillis();
        List<SurviveProcess> initList = new ArrayList();
        initList = processService.findRightLineProcess();
        if(null == initList || initList.size() == 0) {
            return;
        }
        int count=0;
        for (SurviveProcess sp : initList) {
            try{
                boolean flag = false;
                if(ruleService.beforeHandle(sp) && processService.notRemind(sp)) {
                    ProcessInstance processInstance = processService.startProcess(sp);
                    sp.setProcessId(processInstance.getProcessInstanceId());
                    processService.insertCustomProcessAndSendMail(sp);
                    count++;
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
                if(!StringUtils.isEmpty(sp.getProcessId())){
                    processService.cancelPreocess(sp.getProcessId());
                }
                exceptionProcessService.insert(sp);
            }
        }
        log.info("行权付息事项启动流程"+count+"笔,耗时:" + (System.currentTimeMillis()-beginTime));
    }
}