package com.innodealing.bpms.appconfig.history;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CompareField {
    String mark()default "";
    //0-字符串 1-日期(MM-dd) 2-数值int 3-数值BigDecimal 4-日期 5-对象
    int type() default 0;
    //1-自动关联 2-督导确认 3-触发通知 4-付息频率 5-项目状态 6-
    int kind() default 0;
}
