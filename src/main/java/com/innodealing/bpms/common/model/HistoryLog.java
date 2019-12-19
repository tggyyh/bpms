package com.innodealing.bpms.common.model;

import java.util.Date;
import java.util.List;

public class HistoryLog {
    private Date logDate;
    private String logUserId;
    private String logUser;
    private String logMark;
    private int logOperateType;
    private List<String> logContent;

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogUserId() {
        return logUserId;
    }

    public void setLogUserId(String logUserId) {
        this.logUserId = logUserId;
    }

    public String getLogUser() {
        return logUser;
    }

    public void setLogUser(String logUser) {
        this.logUser = logUser;
    }

    public String getLogMark() {
        return logMark;
    }

    public void setLogMark(String logMark) {
        this.logMark = logMark;
    }

    public int getLogOperateType() {
        return logOperateType;
    }

    public void setLogOperateType(int logOperateType) {
        this.logOperateType = logOperateType;
    }

    public List<String> getLogContent() {
        return logContent;
    }

    public void setLogContent(List<String> logContent) {
        this.logContent = logContent;
    }
}
