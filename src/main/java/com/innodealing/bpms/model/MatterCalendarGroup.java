package com.innodealing.bpms.model;

import java.util.List;

public class MatterCalendarGroup {
    private int id;
    private int type;
    private String name;
    private String shortname;
    private String description;
    private String color;
    private List<MatterCalendar> matterCalendarList;

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

    public List<MatterCalendar> getMatterCalendarList() {
        return matterCalendarList;
    }

    public void setMatterCalendarList(List<MatterCalendar> matterCalendarList) {
        this.matterCalendarList = matterCalendarList;
    }
}
