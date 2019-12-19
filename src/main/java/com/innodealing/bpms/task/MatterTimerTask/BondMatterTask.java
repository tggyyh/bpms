package com.innodealing.bpms.task.MatterTimerTask;

import com.innodealing.bpms.model.SurviveProcess;
import com.innodealing.bpms.service.ExceptionProcessService;
import com.innodealing.bpms.service.ProcessService;
import com.innodealing.bpms.task.MatterTask;
import com.innodealing.bpms.task.rule.RuleService;
import org.activiti.engine.RuntimeService;
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
public class BondMatterTask extends MatterTask{
    @Autowired
    private ProcessService processService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ExceptionProcessService exceptionProcessService;
    private static final String REMIND_BOND="bond";
    private static final Logger log = LoggerFactory.getLogger(BondMatterTask.class);

    @Scheduled(cron="0 0 8 * * *")
//    @Scheduled(fixedDelay=5000000)
    public void handle() {
        super.handle(REMIND_BOND);
    }
    @Override
    public void execute(){
        Long beginTime = System.currentTimeMillis();
        List<SurviveProcess> list = new ArrayList();
        List<SurviveProcess> initList = processService.findBondProcess();
        if(null == initList || initList.size() == 0) {
            return;
        }
        int count = 0;
        //对数据处理，写入remindDate,CompleteDate
        for (SurviveProcess sp : initList) {
            try {
                boolean flag = false;
                if (0 == sp.getRuleType()) {
                    flag = ruleService.timeHandle(sp);
                }else if(1 == sp.getRuleType()){
                    flag = ruleService.beforeHandle(sp);
                }
                if(flag && processService.notRemind(sp)){
                    ProcessInstance processInstance = processService.startProcess(sp);
                    sp.setProcessId(processInstance.getProcessInstanceId());
                    processService.insertProcessAndSendMail(sp);
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
        log.info("项目事项启动流程"+count+"笔,耗时:" + (System.currentTimeMillis()-beginTime));
    }
}