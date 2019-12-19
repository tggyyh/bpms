package com.innodealing.bpms.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TemplateRule {
    private int id;
    private int templateId;
    private int type;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date remindDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeDate;
    private int beforeDay;
    private int completeBeforeDay;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getBeforeDay() {
        return beforeDay;
    }

    public void setBeforeDay(int beforeDay) {
        this.beforeDay = beforeDay;
    }

    public int getCompleteBeforeDay() {
        return completeBeforeDay;
    }

    public void setCompleteBeforeDay(int completeBeforeDay) {
        this.completeBeforeDay = completeBeforeDay;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateRule that = (TemplateRule) o;

        if (templateId != that.templateId) return false;
        if (type != that.type) return false;
        if (beforeDay != that.beforeDay) return false;
        if (completeBeforeDay != that.completeBeforeDay) return false;
        if (remindDate != null ? !remindDate.equals(that.remindDate) : that.remindDate != null) return false;
        return completeDate != null ? completeDate.equals(that.completeDate) : that.completeDate == null;

    }

    @Override
    public int hashCode() {
        int result = templateId;
        result = 31 * result + type;
        result = 31 * result + (remindDate != null ? remindDate.hashCode() : 0);
        result = 31 * result + (completeDate != null ? completeDate.hashCode() : 0);
        result = 31 * result + beforeDay;
        result = 31 * result + completeBeforeDay;
        return result;
    }

    @Override
    public String toString() {
        String returnValue = "";
        if(type==0){
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
            String strRemindDate = formatter.format(remindDate);
            String strCompleteDate = formatter.format(completeDate);
            returnValue = "提醒时间：" + strRemindDate + ",需完成时间：" + strCompleteDate;
        }else if(type==1){
            returnValue = "提醒时间：付息日提前" + beforeDay + "工作日,需完成时间：付息日提前" + completeBeforeDay + "工作日";
        }
        return returnValue;
    }
}
