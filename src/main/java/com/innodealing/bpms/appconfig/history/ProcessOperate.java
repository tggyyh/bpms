package com.innodealing.bpms.appconfig.history;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessOperate {
    String mark() default "";
//    1：确认事项 2：项目人员处理事项 3：审核事项
    int taskType() default 0;
//    1:确认事项 2：行权付息确认事项 3：批量确认事项
    int type() default 0;
}

