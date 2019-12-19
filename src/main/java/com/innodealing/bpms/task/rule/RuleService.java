package com.innodealing.bpms.task.rule;

import com.innodealing.bpms.model.SurviveProcess;
import com.innodealing.bpms.service.HolidayService;
import com.innodealing.bpms.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service("ruleService")
public class RuleService {
    @Autowired
    private HolidayService holidayService;

    //按照指定日期处理
    public Boolean  timeHandle(SurviveProcess sp) throws Exception{
        boolean b = false;
        Date current =  new Date();
        Date remindDate = sp.getRemindDate();
        Date completeDate = sp.getCompleteDate();
        Calendar cal = Calendar.getInstance();
        int yearInt = cal.get(Calendar.YEAR);
        String year = String.valueOf(yearInt);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentRemindDateStr = year.concat(sdf.format(remindDate).substring(4));
        Date currentRemindDate = sdf.parse(currentRemindDateStr);
        Date newRemindDate = holidayService.convertHolidayToWorkday(currentRemindDate);
        if (sdf.format(newRemindDate).equals(sdf.format(current))) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("MMdd");
            int remindDateInt = Integer.parseInt(sdf1.format(remindDate));
            int completeDateInt = Integer.parseInt(sdf1.format(completeDate));
            String currentCompleteDateStr="";
            if(completeDateInt>=remindDateInt){
                currentCompleteDateStr = year.concat(sdf.format(completeDate).substring(4));
            }else{
                currentCompleteDateStr = String.valueOf(yearInt+1).concat(sdf.format(completeDate).substring(4));
            }
            Date currentCompleteDate = sdf.parse(currentCompleteDateStr);
            sp.setCompleteDate(holidayService.convertHolidayToWorkday(currentCompleteDate));
            b = true;
        }
        return b;
    }
    //触发规则 T-N
    public boolean beforeHandle(SurviveProcess sp) throws Exception {
        boolean b = false;
        Date current =  new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentStr = sdf.format(current);
        Date currentDay = sdf.parse(currentStr);
        Date payDate = sp.getPayDay();
        Date valueDate = sp.getValueDate();
        int payFrequency = sp.getPayFrequency();
        int before = sp.getBeforeDay();
        int complete = sp.getCompleteBeforeDay();
        while (valueDate.compareTo(payDate)<=0){
            Date realDate = holidayService.convertHolidayToWorkday(
                    holidayService.calculateDate(valueDate,before));
            int result =  realDate.compareTo(currentDay);
            if(0== result){
                b = true;
                sp.setCompleteDate(holidayService.convertHolidayToWorkday(
                        holidayService.calculateDate(valueDate,complete)));
                break;
            }
            if(1 == result){
                break;
            }
            if(-1 == result){
                if(0 == payFrequency){
                    valueDate = dateAdd3Month(valueDate);
                }else if(1 == payFrequency){
                    valueDate = dateAdd1Year(valueDate);
                }
            }
        }
        return b;
    }

    public Date dateSub(Date date,int day){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -day);
        return calendar.getTime();
    }
    public Date dateAdd3Month(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 3);
        return calendar.getTime();
    }
    public Date dateAdd1Year(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1);
        return calendar.getTime();
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2018-02-29");
        System.out.println(sdf.format(date));
    }
}
