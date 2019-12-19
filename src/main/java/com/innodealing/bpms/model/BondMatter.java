package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;

import java.util.Date;

public class BondMatter {
    private int id;
    @CompareField(mark = "")
    private String bondCode;
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

    public String getBondCode() {
        return bondCode;
    }

    public void setBondCode(String bondCode) {
        this.bondCode = bondCode;
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

        BondMatter that = (BondMatter) o;

        if (templateId != that.templateId) return false;
        return bondCode != null ? bondCode.equals(that.bondCode) : that.bondCode == null;

    }

    @Override
    public int hashCode() {
        int result = bondCode != null ? bondCode.hashCode() : 0;
        result = 31 * result + templateId;
        return result;
    }

    @Override
    public String toString() {
        return "BondMatter{" +
                "bondCode='" + bondCode + '\'' +
                ", templateId=" + templateId +
                '}';
    }
}
