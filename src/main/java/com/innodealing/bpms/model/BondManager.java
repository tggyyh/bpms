package com.innodealing.bpms.model;

import java.util.Date;

public class BondManager {
    private int id;
    private String bondCode;
    private String userId;
    private Date createTime;

    private User sysUser;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getSysUser() {
        return sysUser;
    }

    public void setSysUser(User sysUser) {
        this.sysUser = sysUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BondManager that = (BondManager) o;

        if (bondCode != null ? !bondCode.equals(that.bondCode) : that.bondCode != null) return false;
        return userId != null ? userId.equals(that.userId) : that.userId == null;

    }

    @Override
    public int hashCode() {
        int result = bondCode != null ? bondCode.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
