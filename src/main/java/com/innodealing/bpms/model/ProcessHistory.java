package com.innodealing.bpms.model;

import java.util.List;

import java.util.Date;

public class ProcessHistory {
    private long id;
    private String userId;
    private int type;
    private int taskType;
    private String processId;
    private String processInfo;
    private String mark;
    private Date createTime;
    private String name;

    private ProcessInfo pi;
    private List<String> changeList ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(String processInfo) {
        this.processInfo = processInfo;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessInfo getPi() {
        return pi;
    }

    public void setPi(ProcessInfo pi) {
        this.pi = pi;
    }

    public List<String> getChangeList() {
        return changeList;
    }

    public void setChangeList(List<String> changeList) {
        this.changeList = changeList;
    }
}
