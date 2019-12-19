package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class CustomSubMatter {
    private int id;
    private int customId;
    @CompareField(mark = "子事项")
    private int orderIndex;
    @CompareField(mark = "事项名")
    private String name;
    @CompareField(mark = "提醒规则-提醒时间付息日提前")
    private int beforeDay;
    @CompareField(mark = "提醒规则-需完成时间付息日提前")
    private int completeBeforeDay;
    @CompareField(mark = "提醒频率-需完成时间提前")
    private int mailBeforeDay;
    @CompareField(mark = "提醒频率-每隔")
    private int mailFrequency;
    @CompareField(mark = "事项内容")
    private String content;
    private Date createTime;

    private Date beforeDate;
    private Date completeBeforeDate;
    private Date mailBeforeDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date completeDate;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomId() {
        return customId;
    }

    public void setCustomId(int customId) {
        this.customId = customId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMailBeforeDay() {
        return mailBeforeDay;
    }

    public void setMailBeforeDay(int mailBeforeDay) {
        this.mailBeforeDay = mailBeforeDay;
    }

    public int getMailFrequency() {
        return mailFrequency;
    }

    public void setMailFrequency(int mailFrequency) {
        this.mailFrequency = mailFrequency;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getMailBeforeDate() {
        return mailBeforeDate;
    }

    public void setMailBeforeDate(Date mailBeforeDate) {
        this.mailBeforeDate = mailBeforeDate;
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

    public Date getBeforeDate() {
        return beforeDate;
    }

    public void setBeforeDate(Date beforeDate) {
        this.beforeDate = beforeDate;
    }

    public Date getCompleteBeforeDate() {
        return completeBeforeDate;
    }

    public void setCompleteBeforeDate(Date completeBeforeDate) {
        this.completeBeforeDate = completeBeforeDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public int hashCode() {
        int result = customId;
        result = 31 * result + orderIndex;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + beforeDay;
        result = 31 * result + completeBeforeDay;
        result = 31 * result + mailBeforeDay;
        result = 31 * result + mailFrequency;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
