package com.innodealing.bpms.model;

import java.util.Date;

public class SurviveProcess {
    private long id;
    private int type;
    private int matterId;
    private int templateId;
    private int ruleId;
    private int customId;
    private int confirm;
    private int status;
    private String processId;
    private Date createTime;
    private Date endTime;

    private String companyName;
    private String bondCode;
    private String bondShortname;
    private Date remindDate;
    private Date completeDate;

    private int ruleType;
    private int beforeDay;
    private int completeBeforeDay;
    private Date valueDate;

    private Date payDay;
    private int payFrequency;

    private int orderIndex;
    private String subName;
    private int mailFrequency;
    private String content;
    private int mailBeforeDay;

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMatterId() {
        return matterId;
    }

    public void setMatterId(int matterId) {
        this.matterId = matterId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBondCode() {
        return bondCode;
    }

    public void setBondCode(String bondCode) {
        this.bondCode = bondCode;
    }

    public String getBondShortname() {
        return bondShortname;
    }

    public void setBondShortname(String bondShortname) {
        this.bondShortname = bondShortname;
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

    public int getCustomId() {
        return customId;
    }

    public void setCustomId(int customId) {
        this.customId = customId;
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

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
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

    public int getPayFrequency() {
        return payFrequency;
    }

    public void setPayFrequency(int payFrequency) {
        this.payFrequency = payFrequency;
    }

    public Date getPayDay() {
        return payDay;
    }

    public void setPayDay(Date payDay) {
        this.payDay = payDay;
    }

    public int getMailBeforeDay() {
        return mailBeforeDay;
    }

    public void setMailBeforeDay(int mailBeforeDay) {
        this.mailBeforeDay = mailBeforeDay;
    }
}
