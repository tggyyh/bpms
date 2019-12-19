package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.Area;
import com.innodealing.bpms.model.IsHoliday;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface HolidayMapper {
    Integer findIsHoliday(String date);
    List<IsHoliday> findAll();
    List<IsHoliday> findByYear(int year);
}

