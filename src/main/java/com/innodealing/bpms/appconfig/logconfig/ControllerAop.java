package com.innodealing.bpms.appconfig.logconfig;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAop {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAop.class);
    @Pointcut("within(com.innodealing.bpms.controller.*)") // the pointcut expression
    public void logMethod() {
    }

    @Before(value = "logMethod()")
    public void doBefore(JoinPoint pjp) throws Throwable {
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        logger.info(pjp.getSignature().toString()+"操作人"+account);
    }
    @Around(value = "logMethod()")
    public Object doAfter(ProceedingJoinPoint pjp) throws Throwable {
        Long beginTime = System.currentTimeMillis();
        logger.info(pjp.getSignature().toString()+"beginTime:"+beginTime);
        Object retVal = pjp.proceed();
        logger.info(pjp.getSignature().toString()+"endTime:"+(System.currentTimeMillis()-beginTime));
        return retVal;
    }


}
