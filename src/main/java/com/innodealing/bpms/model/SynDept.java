package com.innodealing.bpms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class SynDept {
    private String id;
    private boolean isValid;
    private String name;
    private String parentId;
    private String uniDeptCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty(value="isValid")
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUniDeptCode() {
        return uniDeptCode;
    }

    public void setUniDeptCode(String uniDeptCode) {
        this.uniDeptCode = uniDeptCode;
    }
}
