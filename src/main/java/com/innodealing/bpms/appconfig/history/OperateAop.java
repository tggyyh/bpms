package com.innodealing.bpms.appconfig.history;

import com.google.gson.Gson;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.History;
import com.innodealing.bpms.service.HistoryService;
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

import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class OperateAop {
    @Autowired
    HistoryService historyService;
    private static final Logger logger = LoggerFactory.getLogger(OperateAop.class);
    @Pointcut("@annotation(com.innodealing.bpms.appconfig.history.Operate)") // the pointcut expression
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
            String methodName = sign.getDeclaringTypeName() +  "." + sign.getName();
            Object[] args = point.getArgs();
            Operate operate = ((MethodSignature) point.getSignature()).
                    getMethod().getAnnotation((Operate.class));
            Gson gson = new Gson();
            String jsonString = gson.toJson(args);
            History h = new History();
            h.setArgs(jsonString);
            h.setMark(operate.mark());
            h.setOperateType(operate.operateType());
            int type = operate.type();
            h.setType(type);
            if(type == 1){
                h.setTemplateId((Integer)key);
            }else if(type == 2){
                h.setBondCode((String)key);
            }else if(type == 3){
                h.setCompanyName((String)key);
            }else if(type == 4){
                History history = (History)key;
                h.setCustomId(history.getCustomId());
                h.setBondCode(history.getBondCode());
                h.setCompanyName(history.getCompanyName());
            }else if(type == 101){
                h.setTemplateId((Integer)key);
            }else if(type == 201){
                h.setBondCode((String)key);
            }else if(type == 301){
                h.setCompanyName((String)key);
            }
            h.setMethodName(methodName);
            h.setUserId(userId);
            historyService.insertHistory(h);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
