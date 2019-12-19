package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.HolidayMapper;
import com.innodealing.bpms.model.IsHoliday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("holidayService")
public class HolidayService {
//    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private static final ThreadLocal<DateFormat> df =
            new ThreadLocal<DateFormat>(){
                @Override
                protected DateFormat initialValue(){
                    return new SimpleDateFormat("yyyy-MM-dd");
                }
            };
    @Autowired
    HolidayMapper holidayMapper;
    private static Map<String,Integer> holidayMap = new HashMap();
    public boolean isHoliday(Date date){
        if(holidayMap.isEmpty()){
            List<IsHoliday> list = findAll();
            for(IsHoliday isHoliday:list){
                holidayMap.put(df.get().format(isHoliday.getDate()),isHoliday.getIsHoliday());
            }
        }
        String dateStr = df.get().format(date);
        Integer ih = holidayMap.get(dateStr);

        if(null == ih){
            return false;
        }
        if(1==ih.intValue()){
            return  true;
        }
        return false;
    }

    public Date convertHolidayToWorkday(Date date) {
        while(isHoliday(date)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
            date = calendar.getTime();
        }
        return date;
    }

    public List<IsHoliday> findAll(){
        return holidayMapper.findAll();
    }

    //计算date2-date1 之间有几个工作日;date1<date2,否则返回0
    public int workDay(Date date1 ,Date date2){
        if(date2.compareTo(date1)<0){
            return -1;
        }
        int day=0;
        while(date2.compareTo(date1)>0) {
            if(!isHoliday(date2)){
                day++;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date2);
            calendar.add(Calendar.DATE, -1);
            date2 = calendar.getTime();
        }
        return day;
    }

    public List<IsHoliday> findByYear(int year){
        return holidayMapper.findByYear(year);
    }
    //计算指定日期 date 提前 beforeDay天后 日期。
    public Date calculateDate(Date date, int beforeDay) {
        while(beforeDay>0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
            date = calendar.getTime();
            if(!isHoliday(date)){
                beforeDay--;
            }
        }
        return date;
    }
}
