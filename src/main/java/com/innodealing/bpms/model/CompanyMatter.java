package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;

import java.util.Date;

public class CompanyMatter {
    private int id;
    @CompareField(mark = "")
    private String companyName;
    @CompareField(mark = "")
    private int templateId;
    private int status;
    private Date createTime;

    private MatterTemplate matterTemplate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public MatterTemplate getMatterTemplate() {
        return matterTemplate;
    }

    public void setMatterTemplate(MatterTemplate matterTemplate) {
        this.matterTemplate = matterTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyMatter that = (CompanyMatter) o;

        if (templateId != that.templateId) return false;
        return companyName != null ? companyName.equals(that.companyName) : that.companyName == null;

    }

    @Override
    public int hashCode() {
        int result = companyName != null ? companyName.hashCode() : 0;
        result = 31 * result + templateId;
        return result;
    }

    @Override
    public String toString() {
        return companyName + ": 由 “勾选” 修改为 “未勾选”。";
    }
}
