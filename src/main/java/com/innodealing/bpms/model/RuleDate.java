package com.innodealing.bpms.model;

import java.util.Date;

public class RuleDate {
    private int beforeDay;
    private Date remindDate;
    private Date completeDate;

    public int getBeforeDay() {
        return beforeDay;
    }

    public void setBeforeDay(int beforeDay) {
        this.beforeDay = beforeDay;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }
}
