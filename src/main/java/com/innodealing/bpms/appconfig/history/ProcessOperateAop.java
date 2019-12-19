package com.innodealing.bpms.appconfig.history;

import com.google.gson.Gson;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.ProcessHistory;
import com.innodealing.bpms.service.ProcessHistoryService;
import com.innodealing.commons.http.RestResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class ProcessOperateAop {
    @Autowired
    ProcessHistoryService processHistoryService;
    private static final Logger logger = LoggerFactory.getLogger(ProcessOperateAop.class);
    @Pointcut("@annotation(com.innodealing.bpms.appconfig.history.ProcessOperate)") // the pointcut expression
    public void operateMethod() {
    }

    @AfterReturning(value = "operateMethod()", returning = "rvt")
    public void doAfterReturning(JoinPoint jp, Object rvt) {
        if(null!=rvt){
            RestResponse<String> result = (RestResponse)rvt;
            if(result.getCode()==0){
                doLog(jp, result.getData());
            }
        }
    }

    @AfterThrowing(pointcut = "operateMethod()", throwing = "e")
    public void doAfterThrow(JoinPoint jp, Exception e) {
//        doLog(jp, e);
//        logger.error(e.getMessage(),e);
    }

    private void doLog(JoinPoint point, Object key) {
        //获取登录用户信息
        Subject subject = SecurityUtils.getSubject();
        String userId = (String) subject.getPrincipal();
        try {
            Signature sign = point.getSignature();
            Object[] args = point.getArgs();
            ProcessOperate operate = ((MethodSignature) point.getSignature()).
                    getMethod().getAnnotation((ProcessOperate.class));
            Gson gson = new Gson();
            int type = operate.type();
            int taskType = operate.taskType();
            if(1==type|| 2==taskType||3==taskType){
                MatterTask matterTask = (MatterTask)args[0];
                String jsonString = gson.toJson(matterTask.getProcessInfo());
                ProcessHistory h = new ProcessHistory();
                h.setProcessInfo(jsonString);
                if(3==taskType){
                   if(0 == matterTask.getCheckResult()){
                       h.setMark(operate.mark()+"—退回");
                   }else{
                       h.setMark(operate.mark()+"—终结");
                   }
                }else {
                    h.setMark(operate.mark());
                }
                h.setTaskType(taskType);
                h.setType(type);
                h.setProcessId(matterTask.getProcessId());
                h.setUserId(userId);
                processHistoryService.insert(h);
            }else if(2==type){
                List<MatterTask> mts = (List<MatterTask>)args[0];
                for(MatterTask mt:mts){
                    String jsonString = gson.toJson(mt.getProcessInfo());
                    ProcessHistory h = new ProcessHistory();
                    h.setProcessInfo(jsonString);
                    h.setMark(operate.mark());
                    h.setType(type);
                    h.setTaskType(taskType);
                    h.setProcessId(mt.getProcessId());
                    h.setUserId(userId);
                    processHistoryService.insert(h);
                }
            }else if(3==type){
                List<String> pIds = (ArrayList<String>)key;
                for(String pId:pIds){
                    ProcessHistory h = new ProcessHistory();
                    h.setMark(operate.mark());
                    h.setType(type);
                    h.setTaskType(taskType);
                    h.setProcessId(pId);
                    h.setUserId(userId);
                    processHistoryService.insert(h);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
