package com.innodealing.bpms.appconfig.history;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Operate {
    String mark() default "";
    int type() default 1;   // 1模板 2项目 3发行人 4自定义事项 101模板关联 201项目关联 301发行人关联
    int operateType() default 1; // 1新增 2修改 3删除 4设置到期 5撤消到期
}

