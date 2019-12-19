package com.innodealing.bpms.model;

import java.util.Date;

public class IsHoliday {
    private int id;
    private String year;
    private String monthDay;
    private Date date;
    private int isHoliday;
    private int isCommonHoliday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(int isHoliday) {
        this.isHoliday = isHoliday;
    }

    public int getIsCommonHoliday() {
        return isCommonHoliday;
    }

    public void setIsCommonHoliday(int isCommonHoliday) {
        this.isCommonHoliday = isCommonHoliday;
    }
}
