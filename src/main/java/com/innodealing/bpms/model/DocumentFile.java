package com.innodealing.bpms.model;

import java.util.Date;
import java.util.List;

public class DocumentFile {
    private int id;
    private int type;
    private String companyName;
    private String bondCode;
    private String bondShortname;
    private String description;
    private Date createTime;
    private Date updateTime;
    private int isFiles;
    private int orderIndex;
    private int customId;
    private Date customCreateTime;

    private int status;
    private String userId;
    private String roleCode;
    private String userName;

    private List<DocumentAttachment> documentAttachmentList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getIsFiles() {
        return isFiles;
    }

    public void setIsFiles(int isFiles) {
        this.isFiles = isFiles;
    }

    public List<DocumentAttachment> getDocumentAttachmentList() {
        return documentAttachmentList;
    }

    public void setDocumentAttachmentList(List<DocumentAttachment> documentAttachmentList) {
        this.documentAttachmentList = documentAttachmentList;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setCustomId(int customId) {
        this.customId = customId;
    }

    public int getCustomId() {
        return customId;
    }

    public void setCustomCreateTime(Date customCreateTime) {
        this.customCreateTime = customCreateTime;
    }

    public Date getCustomCreateTime() {
        return customCreateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
