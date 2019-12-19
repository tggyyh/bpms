package com.innodealing.bpms.model;

import com.innodealing.bpms.appconfig.history.CompareField;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class CustomMatter {
    private int id;
    @CompareField(mark = "事项分类")
    private int type;
    @CompareField(mark = "是否行权付息事项")
    private int rightLine;
    private String key;
    @CompareField(mark = "事项名称")
    private String name;
    @CompareField(mark = "事项简称")
    private String shortname;
    @CompareField(mark = "事项说明")
    private String description;
    @CompareField(mark = "事项图标")
    private String color;
    @CompareField(mark = "事项内容")
    private String content;
    @CompareField(mark = "督导确认", type = 2, kind = 2)
    private int confirm;
    @CompareField(mark = "触发通知", type = 2, kind = 3)
    private int mailUser;
    @CompareField(mark = "提醒频率：需完成时间提前")
    private int mailBeforeDay;
    @CompareField(mark = "提醒频率：每隔")
    private int mailFrequency;
    private int status;
    private int relation;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @CompareField(mark = "提醒规则")
    private List<CustomRule> customRuleList;
    @CompareField(mark = "模板材料")
    private List<CustomAttachment> customAttachmentList;
    @CompareField(mark = "", type = 6)
    private List<CustomSubMatter> customSubMatterList;

    private Date mailBeforeDate;

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

    public int getRightLine() {
        return rightLine;
    }

    public void setRightLine(int rightLine) {
        this.rightLine = rightLine;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
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

    public List<CustomRule> getCustomRuleList() {
        return customRuleList;
    }

    public void setCustomRuleList(List<CustomRule> customRuleList) {
        this.customRuleList = customRuleList;
    }

    public List<CustomAttachment> getCustomAttachmentList() {
        return customAttachmentList;
    }

    public void setCustomAttachmentList(List<CustomAttachment> customAttachmentList) {
        this.customAttachmentList = customAttachmentList;
    }

    public List<CustomSubMatter> getCustomSubMatterList() {
        return customSubMatterList;
    }

    public void setCustomSubMatterList(List<CustomSubMatter> customSubMatterList) {
        this.customSubMatterList = customSubMatterList;
    }

    public void setMailBeforeDate(Date mailBeforeDate) {
        this.mailBeforeDate = mailBeforeDate;
    }

    public Date getMailBeforeDate() {
        return mailBeforeDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomMatter that = (CustomMatter) o;

        if (type != that.type) return false;
        if (rightLine != that.rightLine) return false;
        if (confirm != that.confirm) return false;
        if (mailUser != that.mailUser) return false;
        if (mailBeforeDay != that.mailBeforeDay) return false;
        if (mailFrequency != that.mailFrequency) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (shortname != null ? !shortname.equals(that.shortname) : that.shortname != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (customRuleList != null ? !customRuleList.equals(that.customRuleList) : that.customRuleList != null)
            return false;
        if (customAttachmentList != null ? !customAttachmentList.equals(that.customAttachmentList) : that.customAttachmentList != null)
            return false;
        return customSubMatterList != null ? customSubMatterList.equals(that.customSubMatterList) : that.customSubMatterList == null;

    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + rightLine;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortname != null ? shortname.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + confirm;
        result = 31 * result + mailUser;
        result = 31 * result + mailBeforeDay;
        result = 31 * result + mailFrequency;
        result = 31 * result + (customRuleList != null ? customRuleList.hashCode() : 0);
        result = 31 * result + (customAttachmentList != null ? customAttachmentList.hashCode() : 0);
        result = 31 * result + (customSubMatterList != null ? customSubMatterList.hashCode() : 0);
        return result;
    }
}
