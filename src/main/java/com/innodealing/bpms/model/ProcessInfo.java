package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class ProcessInfo {
    private long id;
    private int type;
    private int status;
    private String processId;
    private String companyName;
    private String bondCode;
    private String bondShortname;
    private String name;
    private String shortname;
    private String description;
    private String color;
    @CompareField(mark = "事项内容")
    private String content;
    private String rightLineContent;
    private String reason;
    private String mark;
    private int confirm;
    private int mailBeforeDay;
    private int mailUser;
    private int noticeFlag;
    private int mailFrequency;
    private Integer autoRelate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date remindDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date completeDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private List<TemplateRule> templateRuleList;
    @CompareField(mark = "模板材料")
    private List<ProcessTemplateAttachment> processTemplateAttachmentList;
    private List<ProcessAttachment> processAttachmentList;

    private String subName;
    private int subBeforeDay;
    private int subCompleteBeforeDay;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<TemplateRule> getTemplateRuleList() {
        return templateRuleList;
    }

    public void setTemplateRuleList(List<TemplateRule> templateRuleList) {
        this.templateRuleList = templateRuleList;
    }

    public List<ProcessTemplateAttachment> getProcessTemplateAttachmentList() {
        return processTemplateAttachmentList;
    }

    public void setProcessTemplateAttachmentList(List<ProcessTemplateAttachment> processTemplateAttachmentList) {
        this.processTemplateAttachmentList = processTemplateAttachmentList;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ProcessAttachment> getProcessAttachmentList() {
        return processAttachmentList;
    }

    public void setProcessAttachmentList(List<ProcessAttachment> processAttachmentList) {
        this.processAttachmentList = processAttachmentList;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public int getMailUser() {
        return mailUser;
    }

    public void setMailUser(int mailUser) {
        this.mailUser = mailUser;
    }

    public int getMailFrequency() {
        return mailFrequency;
    }

    public void setMailFrequency(int mailFrequency) {
        this.mailFrequency = mailFrequency;
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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getNoticeFlag() {
        return noticeFlag;
    }

    public void setNoticeFlag(int noticeFlag) {
        this.noticeFlag = noticeFlag;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getRightLineContent() {
        return rightLineContent;
    }

    public void setRightLineContent(String rightLineContent) {
        this.rightLineContent = rightLineContent;
    }

    public Integer getAutoRelate() {
        return autoRelate;
    }

    public void setAutoRelate(Integer autoRelate) {
        this.autoRelate = autoRelate;
    }

    public int getMailBeforeDay() {
        return mailBeforeDay;
    }

    public void setMailBeforeDay(int mailBeforeDay) {
        this.mailBeforeDay = mailBeforeDay;
    }

    public int getSubBeforeDay() {
        return subBeforeDay;
    }

    public void setSubBeforeDay(int subBeforeDay) {
        this.subBeforeDay = subBeforeDay;
    }

    public int getSubCompleteBeforeDay() {
        return subCompleteBeforeDay;
    }

    public void setSubCompleteBeforeDay(int subCompleteBeforeDay) {
        this.subCompleteBeforeDay = subCompleteBeforeDay;
    }
}
