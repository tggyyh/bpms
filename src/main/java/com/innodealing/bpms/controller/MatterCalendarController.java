package com.innodealing.bpms.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.HolidayService;
import com.innodealing.bpms.service.MatterCalendarService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.bpms.unit.DateFormat;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mattercalendar")
public class MatterCalendarController {

    @Autowired
    MatterCalendarService matterCalendarService;
    @Autowired
    HolidayService holidayService;
    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MatterCalendarController.class);
    private final static String OrgViewPrefix = "app/bpms/matter-calendar/";

    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(Model model){
        return OrgViewPrefix + "index";
    }

    @RequestMapping(value="/matter", method = RequestMethod.GET)
    public String matter(Model model){
        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        return OrgViewPrefix + "matter";
    }

    @ResponseBody
    @RequestMapping(value = "/findCalendarData", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findCalendarData(@RequestBody(required = false) ReqData reqData){
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        PageInfo<MatterCalendar> page = matterCalendarService.findCalendarData(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/findCalendarMonthComplete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public List<MatterCalendarDate> findCalendarMonthComplete(@RequestBody(required = false) ReqData reqData){

        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        List<MatterCalendarDate> matterCalendarDateList = new ArrayList<>();
        List<IsHoliday> isHolidayList = holidayService.findAll();

        String strDate = reqData.getString("monthDate");
        int status = reqData.getInteger("status").intValue();

        Date inDate = DateFormat.convert2Date(strDate, "yyyy-MM-dd");

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(inDate);

        Calendar sDate = Calendar.getInstance();
        sDate.setTime(inDate);
        sDate.add(Calendar.MONTH,-1);

        Calendar eDate = Calendar.getInstance();
        eDate.setTime(inDate);
        eDate.add(Calendar.MONTH,1);

        reqData.put("stime", DateFormat.convert2String(sDate.getTime(), "yyyy-MM-dd"));
        reqData.put("etime", DateFormat.convert2String(eDate.getTime(), "yyyy-MM-dd"));

        List<MatterCalendar> matterCalendarList = new ArrayList<>();
        //已完成的事项
        List<MatterCalendar> processCompleteList = matterCalendarService.findProcessCompleteData(reqData);

        if(status==0){
            matterCalendarDateList = matterCalendarService.findMonthProcessDate(reqData);
            matterCalendarList = matterCalendarService.findMonthCompleteMatter(reqData);
        }else if(status==1){
            matterCalendarList = matterCalendarService.findMonthCompleteMatter(reqData);
        }else{
            matterCalendarDateList = matterCalendarService.findMonthProcessDate(reqData);
        }

        for(MatterCalendar matterCalendar : matterCalendarList){

            if(matterCalendar.getType()==0){
                //发行人事项
                if(null!=matterCalendar.getCompleteDate()){
                    Calendar stepDate = Calendar.getInstance();
                    stepDate.setTime(matterCalendar.getCompleteDate());
                    int compStep =  sDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                    boolean difFlag = true;
                    while(difFlag){
                        //Calendar stepDate = Calendar.getInstance();
                        //stepDate.setTime(matterCalendar.getCompleteDate());
                        stepDate.add(Calendar.YEAR, compStep);
                        //节假日表里是否有设置日期
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                            //节假日表里有效的工作日
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                if(maxDate.getTime()>=sDate.getTime().getTime() && maxDate.getTime()<=eDate.getTime().getTime()){
                                    difFlag = false;
                                    if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==maxDate).count()<=0){
                                        if(isExist(matterCalendar, processCompleteList, maxDate)){
                                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                                            matterCalendarDate.setCalendarDate(maxDate);
                                            matterCalendarDateList.add(matterCalendarDate);
                                        }

                                    }
                                }else if(maxDate.getTime()>eDate.getTime().getTime()){
                                    difFlag = false;
                                }
                            }
                        }else{
                            if(stepDate.getTime().getTime()>=sDate.getTime().getTime() && stepDate.getTime().getTime()<=eDate.getTime().getTime()){
                                difFlag = false;
                                if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==stepDate.getTime()).count()<=0){
                                    if(isExist(matterCalendar, processCompleteList, stepDate.getTime())){
                                        MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                                        matterCalendarDate.setCalendarDate(stepDate.getTime());
                                        matterCalendarDateList.add(matterCalendarDate);
                                    }
                                }
                            }else if(stepDate.getTime().getTime()>eDate.getTime().getTime()){
                                difFlag = false;
                            }
                        }
                        compStep += 1;
                    }
                }
            }else if(matterCalendar.getType()==1){
                //项目事项
                if(matterCalendar.getRuleType()==0){
                    if(null!=matterCalendar.getCompleteDate()){
                        //按固定日期触发
                        Calendar stepDate = Calendar.getInstance();
                        stepDate.setTime(matterCalendar.getCompleteDate());
                        int compStep =  sDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                        boolean difFlag = true;
                        while(difFlag){
                            //Calendar stepDate = Calendar.getInstance();
                            //stepDate.setTime(matterCalendar.getCompleteDate());
                            stepDate.add(Calendar.YEAR, compStep);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    if(maxDate.getTime()>=sDate.getTime().getTime() && maxDate.getTime()<=eDate.getTime().getTime()){
                                        difFlag = false;
                                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==maxDate).count()<=0){
                                            if(isExist(matterCalendar, processCompleteList, maxDate)){
                                                MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                                                matterCalendarDate.setCalendarDate(maxDate);
                                                matterCalendarDateList.add(matterCalendarDate);
                                            }
                                        }
                                    }else if(maxDate.getTime()>eDate.getTime().getTime()){
                                        difFlag = false;
                                    }
                                }
                            }else{
                                if(stepDate.getTime().getTime()>=sDate.getTime().getTime() && stepDate.getTime().getTime()<=eDate.getTime().getTime()){
                                    difFlag = false;
                                    if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==stepDate.getTime()).count()<=0){
                                        if(isExist(matterCalendar, processCompleteList, stepDate.getTime())){
                                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                                            matterCalendarDate.setCalendarDate(stepDate.getTime());
                                            matterCalendarDateList.add(matterCalendarDate);
                                        }
                                    }
                                }else if(stepDate.getTime().getTime()>eDate.getTime().getTime()){
                                    difFlag = false;
                                }
                            }
                            compStep += 1;
                        }
                    }

                }else if(matterCalendar.getRuleType()==1){
                    //按T-N触发
                    //提醒频率
                    if(matterCalendar.getPayFrequency()==0){
                        //按季度
                        boolean isFlag = true;
                        int step = 0;

                        //int valueDay = valueDate.get(Calendar.DATE);
                        while(isFlag){
                            //下个季度触发日期
                            Calendar nextDate = Calendar.getInstance();
                            nextDate.setTime(matterCalendar.getValueDate());
                            nextDate.add(Calendar.MONTH, step*3);

                            int stepCompleteBeforeDay = matterCalendar.getCompleteBeforeDay();
                            while (stepCompleteBeforeDay > 0){
                                nextDate.add(Calendar.DATE, -1);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        nextDate.setTime(maxDate);
                                    }
                                }
                                stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
                            }
                            //最终触发日期是否在有效日期内
                            if(nextDate.getTime().getTime() >= matterCalendar.getValueDate().getTime() && nextDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                if(nextDate.getTime().getTime() >= sDate.getTime().getTime() && nextDate.getTime().getTime() <= eDate.getTime().getTime()){
                                    isFlag = false;
                                    if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==nextDate.getTime()).count()<=0){
                                        if(isExist(matterCalendar, processCompleteList, nextDate.getTime())){
                                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                                            matterCalendarDate.setCalendarDate(nextDate.getTime());
                                            matterCalendarDateList.add(matterCalendarDate);
                                        }
                                    }
                                }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                    isFlag = false;
                                }
                            }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                isFlag = false;
                            }
                            step += 1;
                        }

                    }else if(matterCalendar.getPayFrequency()==1){
                        //按年
                        boolean isFlag = true;
                        int step = 0;

                        //int valueDay = valueDate.get(Calendar.DATE);
                        while(isFlag){
                            //下个年度触发日期
                            Calendar nextDate = Calendar.getInstance();
                            nextDate.setTime(matterCalendar.getValueDate());
                            nextDate.add(Calendar.YEAR, step);

                            int stepCompleteBeforeDay = matterCalendar.getCompleteBeforeDay();
                            while (stepCompleteBeforeDay > 0){
                                nextDate.add(Calendar.DATE, -1);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        nextDate.setTime(maxDate);
                                    }
                                }
                                stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
                            }
                            //最终触发日期是否在有效日期内
                            if(nextDate.getTime().getTime() >= matterCalendar.getValueDate().getTime() && nextDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                if(nextDate.getTime().getTime() >= sDate.getTime().getTime() && nextDate.getTime().getTime() <= eDate.getTime().getTime()){
                                    isFlag = false;
                                    if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==nextDate.getTime()).count()<=0){
                                        if(isExist(matterCalendar, processCompleteList, nextDate.getTime())){
                                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                                            matterCalendarDate.setCalendarDate(nextDate.getTime());
                                            matterCalendarDateList.add(matterCalendarDate);
                                        }
                                    }
                                }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                    isFlag = false;
                                }
                            }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                isFlag = false;
                            }
                            step += 1;
                        }
                    }
                }
            }
        }

        return matterCalendarDateList;
    }

    @ResponseBody
    @RequestMapping(value = "/findCalendarDayComplete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findCalendarDayComplete(@RequestBody(required = false) ReqData reqData){

        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        List<IsHoliday> isHolidayList = holidayService.findAll();

        String strDate = reqData.getString("completeDate");
        int status = reqData.getInteger("status").intValue();

        Date inDate = DateFormat.convert2Date(strDate, "yyyy-MM-dd");

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(inDate);

        reqData.put("stime", strDate);
        reqData.put("etime", strDate);

        //未触发的事项
        List<MatterCalendar> matterCalendarList = matterCalendarService.findMonthCompleteMatter(reqData);
        //已触发的事项
        List<MatterCalendar> processCalendarList = matterCalendarService.findProcessDay(reqData);

        //记录最终数据
        List<MatterCalendar> dayCalendarList = new ArrayList<>();

        if(null!=matterCalendarList && matterCalendarList.size()>0){
            for(MatterCalendar matterCalendar : matterCalendarList){

                if(matterCalendar.getType()==0){
                    //发行人事项
                    //需完成时间
                    if(null!=matterCalendar.getCompleteDate()){
                        Calendar stepDate = Calendar.getInstance();
                        stepDate.setTime(matterCalendar.getCompleteDate());
                        int compStep =  nowDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                        boolean difFlag = true;
                        while(difFlag){
                            //Calendar stepDate = Calendar.getInstance();
                            //stepDate.setTime(matterCalendar.getCompleteDate());
                            stepDate.add(Calendar.YEAR, compStep);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    if(maxDate.getTime() >= nowDate.getTime().getTime()){
                                        difFlag = false;
                                        matterCalendar.setCompleteDate1(maxDate);
                                    }
                                }
                            }else{
                                if(stepDate.getTime().getTime() >= nowDate.getTime().getTime()){
                                    difFlag = false;
                                    matterCalendar.setCompleteDate1(stepDate.getTime());
                                }
                            }
                            //计算提醒日
                            if(!difFlag){
                                Calendar rDate = Calendar.getInstance();
                                rDate.setTime(matterCalendar.getRemindDate());
                                rDate.set(Calendar.YEAR, stepDate.get(Calendar.YEAR));
                                if(rDate.getTime().getTime()>stepDate.getTime().getTime()){
                                    rDate.add(Calendar.YEAR, -1);
                                }
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==rDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=rDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date rMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=rDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        matterCalendar.setRemindDate1(rMaxDate);
                                    }
                                }else{
                                    matterCalendar.setRemindDate1(rDate.getTime());
                                }
                            }
                            compStep += 1;
                        }
                    }

                }else if(matterCalendar.getType()==1){
                    //项目事项
                    if(matterCalendar.getRuleType()==0){
                        if(null!=matterCalendar.getCompleteDate()){
                            //按固定日期触发
                            boolean difFlag = true;
                            int compStep = 0;
                            while(difFlag){
                                Calendar stepDate = Calendar.getInstance();
                                stepDate.setTime(matterCalendar.getCompleteDate());
                                stepDate.add(Calendar.YEAR, compStep);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        if(maxDate.getTime() >= nowDate.getTime().getTime() || maxDate.getTime()>matterCalendar.getPayDay().getTime()){
                                            difFlag = false;
                                            matterCalendar.setCompleteDate1(maxDate);
                                        }
                                    }
                                }else{
                                    if(stepDate.getTime().getTime() >= nowDate.getTime().getTime() || stepDate.getTime().getTime()>matterCalendar.getPayDay().getTime()){
                                        difFlag = false;
                                        matterCalendar.setCompleteDate1(stepDate.getTime());
                                    }
                                }
                                //计算提醒日
                                if(!difFlag){
                                    Calendar rDate = Calendar.getInstance();
                                    rDate.setTime(matterCalendar.getRemindDate());
                                    rDate.set(Calendar.YEAR, stepDate.get(Calendar.YEAR));
                                    if(rDate.getTime().getTime()>stepDate.getTime().getTime()){
                                        rDate.add(Calendar.YEAR, -1);
                                    }
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==rDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=rDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date rMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=rDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            matterCalendar.setRemindDate1(rMaxDate);
                                        }
                                    }else{
                                        matterCalendar.setRemindDate1(rDate.getTime());
                                    }
                                }
                                compStep += 1;
                            }
                        }

                    }else if(matterCalendar.getRuleType()==1){
                        //按T-N触发
                        //提醒频率
                        if(matterCalendar.getPayFrequency()==0){
                            //按季度
                            boolean isFlag = true;
                            int step = 0;

                            //int valueDay = valueDate.get(Calendar.DATE);
                            while(isFlag){
                                //下个季度触发日期
                                Calendar nextRDate = Calendar.getInstance();
                                Calendar nextCDate = Calendar.getInstance();
                                nextCDate.setTime(matterCalendar.getValueDate());
                                nextCDate.add(Calendar.MONTH, step*3);
                                nextRDate.setTime(nextCDate.getTime());

                                int stepCompleteBeforeDay = matterCalendar.getCompleteBeforeDay();
                                while (stepCompleteBeforeDay > 0){
                                    nextCDate.add(Calendar.DATE, -1);
                                    //节假日表里是否有设置日期
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            nextCDate.setTime(maxDate);
                                        }
                                    }
                                    stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
                                }
                                //最终触发日期是否在有效日期内
                                if(nextCDate.getTime().getTime() >= matterCalendar.getValueDate().getTime() && nextCDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                    //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                    if(nextCDate.getTime().getTime() >= nowDate.getTime().getTime()){
                                        matterCalendar.setCompleteDate1(nextCDate.getTime());
                                        isFlag = false;
                                    }
                                }else{
                                    matterCalendar.setCompleteDate1(nextCDate.getTime());
                                    isFlag = false;
                                }
                                //计算提醒日
                                if(!isFlag){
                                    int stepRemindBeforeDay = matterCalendar.getRemindBeforeDay();
                                    while (stepRemindBeforeDay > 0){
                                        nextRDate.add(Calendar.DATE, -1);
                                        //节假日表里是否有设置日期
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                                            //节假日表里有效的工作日
                                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                                Date rMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                                nextRDate.setTime(rMaxDate);
                                            }
                                        }
                                        stepRemindBeforeDay = stepRemindBeforeDay - 1;
                                    }
                                    matterCalendar.setRemindDate1(nextRDate.getTime());
                                }
                                step += 1;
                            }
                        }else if(matterCalendar.getPayFrequency()==1){
                            //按年
                            boolean isFlag = true;
                            int step = 0;

                            //int valueDay = valueDate.get(Calendar.DATE);
                            while(isFlag){
                                //下个年度触发日期
                                Calendar nextRDate = Calendar.getInstance();
                                Calendar nextCDate = Calendar.getInstance();
                                nextCDate.setTime(matterCalendar.getValueDate());
                                nextCDate.add(Calendar.YEAR, step);
                                nextRDate.setTime(nextCDate.getTime());

                                int stepCompleteBeforeDay = matterCalendar.getCompleteBeforeDay();
                                while (stepCompleteBeforeDay > 0){
                                    nextCDate.add(Calendar.DATE, -1);
                                    //节假日表里是否有设置日期
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            nextCDate.setTime(maxDate);
                                        }
                                    }
                                    stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
                                }
                                //最终触发日期是否在有效日期内
                                if(nextCDate.getTime().getTime() >= matterCalendar.getValueDate().getTime() && nextCDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                    //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                    if(nextCDate.getTime().getTime() >= nowDate.getTime().getTime()){
                                        matterCalendar.setCompleteDate1(nextCDate.getTime());
                                        isFlag = false;
                                    }
                                }else{
                                    matterCalendar.setCompleteDate1(nextCDate.getTime());
                                    isFlag = false;
                                }
                                //计算提醒日
                                if(!isFlag){
                                    int stepRemindBeforeDay = matterCalendar.getRemindBeforeDay();
                                    while (stepRemindBeforeDay > 0){
                                        nextRDate.add(Calendar.DATE, -1);
                                        //节假日表里是否有设置日期
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                                            //节假日表里有效的工作日
                                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                                Date rMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                                nextRDate.setTime(rMaxDate);
                                            }
                                        }
                                        stepRemindBeforeDay = stepRemindBeforeDay - 1;
                                    }
                                    matterCalendar.setRemindDate1(nextRDate.getTime());
                                }
                                step += 1;
                            }
                        }
                    }
                }
            }
        }

        for(MatterCalendar matterCalendar : matterCalendarList){
            if(null!=matterCalendar.getCompleteDate1() && matterCalendar.getCompleteDate1().getTime()==nowDate.getTime().getTime()){
                if(matterCalendar.getType()==0 && matterCalendar.getTempId()>0){
                    //发行人事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                            && m.getCompanyName().equals(matterCalendar.getCompanyName())
                            && m.getTempId()==matterCalendar.getTempId()
                            && m.getRuleId()==matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==matterCalendar.getCompleteDate1().getTime()).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }else if(matterCalendar.getType()==0 && matterCalendar.getCustTempId()>0){
                    //发行人算定义事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getCompanyName().equals(matterCalendar.getCompanyName())
                                    && m.getCustTempId()==matterCalendar.getCustTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getCompleteDate1().getTime()==matterCalendar.getCompleteDate1().getTime()).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }else if(matterCalendar.getType()==1 && matterCalendar.getTempId()>0){
                    //项目事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                            && m.getBondCode().equals(matterCalendar.getBondCode())
                            && m.getTempId()==matterCalendar.getTempId()
                            && m.getRuleId()==matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==matterCalendar.getCompleteDate1().getTime()).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }else if(matterCalendar.getType()==1 && matterCalendar.getCustTempId()>0){
                    //项目自定义事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                            && m.getBondCode().equals(matterCalendar.getBondCode())
                            && m.getCustTempId()==matterCalendar.getCustTempId()
                            && m.getRuleId()==matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==matterCalendar.getCompleteDate1().getTime()
                            && m.getOrderIndex()==matterCalendar.getOrderIndex()
                        ).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }

            }
        }

        if(null!=processCalendarList && processCalendarList.size()>0){
            if(status==2){
                List<MatterCalendar> proCalendarList = processCalendarList.stream().filter(m -> m.getStatus()==1).collect(Collectors.toList());
                dayCalendarList.addAll(proCalendarList);
            }else if(status==3){
                List<MatterCalendar> proCalendarList = processCalendarList.stream().filter(m -> m.getStatus()==2 || m.getStatus()==8).collect(Collectors.toList());
                dayCalendarList.addAll(proCalendarList);
            }else if(status==4){
                List<MatterCalendar> proCalendarList = processCalendarList.stream().filter(m -> m.getStatus()==4 || m.getStatus()==16).collect(Collectors.toList());
                dayCalendarList.addAll(proCalendarList);
            }else{
                List<MatterCalendar> proCalendarList = processCalendarList.stream().filter(m -> m.getStatus()<32).collect(Collectors.toList());
                dayCalendarList.addAll(proCalendarList);
            }
        }

        List<MatterCalendarGroup> matterCalendarGroupList = new ArrayList<>();

        //模板事项
        Map<Integer, List<MatterCalendar>> matterCalendarMap = dayCalendarList.stream().filter(m -> m.getCustTempId()<=0).collect(Collectors.groupingBy(e -> e.getTempId()));
        Iterator it = matterCalendarMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<MatterCalendar>> mc = (Map.Entry) it.next();

            MatterCalendarGroup matterCalendarGroup = new MatterCalendarGroup();
            matterCalendarGroup.setId(mc.getValue().get(0).getTempId());
            matterCalendarGroup.setType(mc.getValue().get(0).getType());
            matterCalendarGroup.setName(mc.getValue().get(0).getTempName());
            matterCalendarGroup.setShortname(mc.getValue().get(0).getTempShortname());
            matterCalendarGroup.setDescription(mc.getValue().get(0).getTempDescription());
            matterCalendarGroup.setColor(mc.getValue().get(0).getNowTempColor());
            matterCalendarGroup.setMatterCalendarList(mc.getValue());
            matterCalendarGroupList.add(matterCalendarGroup);
        }

        //自定义模板事项
        dayCalendarList.stream().forEach(calendar -> {
            if(calendar.getCustTempId()>0){
                MatterCalendarGroup matterCalendarGroup = new MatterCalendarGroup();
                matterCalendarGroup.setId(calendar.getTempId());
                matterCalendarGroup.setType(3);
                matterCalendarGroup.setName(calendar.getTempName());
                matterCalendarGroup.setShortname(calendar.getTempShortname());
                matterCalendarGroup.setDescription(calendar.getTempDescription());
                matterCalendarGroup.setColor(calendar.getNowTempColor());
                List<MatterCalendar> itemMatter = new ArrayList<MatterCalendar>();
                itemMatter.add(calendar);
                matterCalendarGroup.setMatterCalendarList(itemMatter);
                matterCalendarGroupList.add(matterCalendarGroup);
            }
        });


        int pageCount = 0;
        if(null!=matterCalendarGroupList && matterCalendarGroupList.size()>0){
            pageCount = matterCalendarGroupList.size();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", pageCount);

        Integer pageNum = 0;
        Integer pageSize = 10;
        if (reqData.getInteger("pNum") != null) {
            pageNum = reqData.getInteger("pNum");
        }
        if (reqData.getInteger("pSize") != null) {
            pageSize = reqData.getInteger("pSize");
        }
        pageNum = pageNum * pageSize;
        //pageSize = pageNum + pageSize;
        List<MatterCalendarGroup> rows = matterCalendarGroupList.stream()
                //.sorted((x,y) -> {return null==x.getRemindDate1() ? -1 : (x.getRemindDate1().getTime()>=y.getRemindDate1().getTime() ? -1 : 1);})
                .skip(pageNum).limit(pageSize).collect(Collectors.toList());
        map.put("rows", rows);

        return map;
    }


    @ResponseBody
    @RequestMapping(value = "/findCalendarMonthRemind", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public List<MatterCalendarDate> findCalendarMonthRemind(@RequestBody(required = false) ReqData reqData){

        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        List<MatterCalendarDate> matterCalendarDateList = new ArrayList<>();
        List<IsHoliday> isHolidayList = holidayService.findAll();

        String strDate = reqData.getString("monthDate");
        String status = reqData.getString("status");

        Date inDate = DateFormat.convert2Date(strDate, "yyyy-MM-dd");

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(inDate);

        Calendar sDate = Calendar.getInstance();
        sDate.setTime(inDate);
        sDate.add(Calendar.MONTH,-1);

        Calendar eDate = Calendar.getInstance();
        eDate.setTime(inDate);
        eDate.add(Calendar.MONTH,1);

        reqData.put("stime", DateFormat.convert2String(sDate.getTime(), "yyyy-MM-dd"));
        reqData.put("etime", DateFormat.convert2String(eDate.getTime(), "yyyy-MM-dd"));

        List<MatterCalendar> matterCalendarList = matterCalendarService.findMonthRemindMatter(reqData);
        List<MatterCalendar> matterCalendarTotal = new ArrayList<>();


        for(MatterCalendar matterCalendar : matterCalendarList){

            if(matterCalendar.getType()==0){
                //发行人事项
                if(null!=matterCalendar.getRemindDate()){
                    Calendar stepDate = Calendar.getInstance();
                    stepDate.setTime(matterCalendar.getRemindDate());
                    int compStep =  nowDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                    boolean difFlag = true;
                    while(difFlag){
                        //Calendar stepDate = Calendar.getInstance();
                        //stepDate.setTime(matterCalendar.getRemindDate());
                        stepDate.add(Calendar.YEAR, compStep);
                        //节假日表里是否有设置日期
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                            //节假日表里有效的工作日
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                if(maxDate.getTime()>=sDate.getTime().getTime() && maxDate.getTime()<=eDate.getTime().getTime()){
                                    difFlag = false;
                                    MatterCalendar mc = matterCalendar;
                                    mc.setRemindDate1(maxDate);
                                    matterCalendarTotal.add(mc);
//                                if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==maxDate).count()<=0){
//                                    MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
//                                    matterCalendarDate.setCalendarDate(maxDate);
//                                    matterCalendarDateList.add(matterCalendarDate);
//                                }

                                }else if(maxDate.getTime()>eDate.getTime().getTime()){
                                    difFlag = false;
                                }
                            }

                        }else{
                            if(stepDate.getTime().getTime()>=sDate.getTime().getTime() && stepDate.getTime().getTime()<=eDate.getTime().getTime()){
                                difFlag = false;
                                MatterCalendar mc = matterCalendar;
                                mc.setRemindDate1(stepDate.getTime());
                                matterCalendarTotal.add(mc);
//                                if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==stepDate.getTime()).count()<=0){
//                                    MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
//                                    matterCalendarDate.setCalendarDate(stepDate.getTime());
//                                    matterCalendarDateList.add(matterCalendarDate);
//                                }

                            }else if(stepDate.getTime().getTime()>eDate.getTime().getTime()){
                                difFlag = false;
                            }
                        }
                        compStep += 1;
                    }
                }
            } else if(matterCalendar.getType()==1){
                //项目事项
                if(matterCalendar.getRuleType()==0){
                    if(null!=matterCalendar.getRemindDate()){
                        //按固定日期触发
                        Calendar stepDate = Calendar.getInstance();
                        stepDate.setTime(matterCalendar.getRemindDate());
                        int compStep =  nowDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                        boolean difFlag = true;
                        while(difFlag){
                            //Calendar stepDate = Calendar.getInstance();
                            //stepDate.setTime(matterCalendar.getRemindDate());
                            stepDate.add(Calendar.YEAR, compStep);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    if(maxDate.getTime()>=sDate.getTime().getTime() && maxDate.getTime()<=eDate.getTime().getTime()){
                                        difFlag = false;
                                        MatterCalendar mc = matterCalendar;
                                        mc.setRemindDate1(maxDate);
                                        matterCalendarTotal.add(mc);
//                                    if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==maxDate).count()<=0){
//                                        MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
//                                        matterCalendarDate.setCalendarDate(maxDate);
//                                        matterCalendarDateList.add(matterCalendarDate);
//                                    }
                                    }else if(maxDate.getTime()>eDate.getTime().getTime()){
                                        difFlag = false;
                                    }
                                }
                            }else{
                                if(stepDate.getTime().getTime()>=sDate.getTime().getTime() && stepDate.getTime().getTime()<=eDate.getTime().getTime()){
                                    difFlag = false;
                                    MatterCalendar mc = matterCalendar;
                                    mc.setRemindDate1(stepDate.getTime());
                                    matterCalendarTotal.add(mc);
//                                    if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==stepDate.getTime()).count()<=0){
//                                        MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
//                                        matterCalendarDate.setCalendarDate(stepDate.getTime());
//                                        matterCalendarDateList.add(matterCalendarDate);
//                                    }
                                }else if(stepDate.getTime().getTime()>eDate.getTime().getTime()){
                                    difFlag = false;
                                }
                            }
                            compStep += 1;
                        }
                    }

                }else if(matterCalendar.getRuleType()==1){
                    //按T-N触发
                    //提醒频率
                    if(matterCalendar.getPayFrequency()==0){
                        //按季度
                        boolean isFlag = true;
                        int step = 0;

                        //int valueDay = valueDate.get(Calendar.DATE);
                        while(isFlag){
                            //下个季度触发日期
                            Calendar nextDate = Calendar.getInstance();
                            nextDate.setTime(matterCalendar.getValueDate());
                            nextDate.add(Calendar.MONTH, step*3);

                            int stepRemindBeforeDay = matterCalendar.getRemindBeforeDay();
                            while (stepRemindBeforeDay > 0){
                                nextDate.add(Calendar.DATE, -1);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        nextDate.setTime(maxDate);
                                    }
                                }
                                stepRemindBeforeDay = stepRemindBeforeDay - 1;
                            }
                            //最终触发日期是否在有效日期内
                            if(nextDate.getTime().getTime() >= matterCalendar.getValueDate().getTime() && nextDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                if(nextDate.getTime().getTime() >= sDate.getTime().getTime() && nextDate.getTime().getTime() <= eDate.getTime().getTime()){
                                    isFlag = false;
                                    MatterCalendar mc = matterCalendar;
                                    mc.setRemindDate1(nextDate.getTime());
                                    matterCalendarTotal.add(mc);
//                                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==nextDate.getTime()).count()<=0){
//                                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
//                                            matterCalendarDate.setCalendarDate(nextDate.getTime());
//                                            matterCalendarDateList.add(matterCalendarDate);
//                                        }
                                }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                    isFlag = false;
                                }
                            }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                isFlag = false;
                            }
                            step += 1;
                        }

                    }else if(matterCalendar.getPayFrequency()==1){
                        //按年
                        boolean isFlag = true;
                        int step = 0;

                        //int valueDay = valueDate.get(Calendar.DATE);
                        while(isFlag){
                            //下个年度触发日期
                            Calendar nextDate = Calendar.getInstance();
                            nextDate.setTime(matterCalendar.getValueDate());
                            nextDate.add(Calendar.YEAR, step);

                            int stepRemindBeforeDay = matterCalendar.getRemindBeforeDay();
                            while (stepRemindBeforeDay > 0){
                                nextDate.add(Calendar.DATE, -1);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        nextDate.setTime(maxDate);
                                    }
                                }
                                stepRemindBeforeDay = stepRemindBeforeDay - 1;
                            }
                            //最终触发日期是否在有效日期内
                            if(nextDate.getTime().getTime() >= matterCalendar.getValueDate().getTime() && nextDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                if(nextDate.getTime().getTime() >= sDate.getTime().getTime() && nextDate.getTime().getTime() <= eDate.getTime().getTime()){
                                    isFlag = false;
                                    MatterCalendar mc = matterCalendar;
                                    mc.setRemindDate1(nextDate.getTime());
                                    matterCalendarTotal.add(mc);
//                                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate()==nextDate.getTime()).count()<=0){
//                                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
//                                            matterCalendarDate.setCalendarDate(nextDate.getTime());
//                                            matterCalendarDateList.add(matterCalendarDate);
//                                        }
                                }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                    isFlag = false;
                                }
                            }else if(nextDate.getTime().getTime()>eDate.getTime().getTime()){
                                isFlag = false;
                            }
                            step += 1;
                        }
                    }
                }
            }
        }


        //已触发的事项
        List<MatterCalendar> processCalendarList = matterCalendarService.findMonthRemindProcess(reqData);
        for(MatterCalendar matterCalendar : matterCalendarTotal){
            //if(null!=matterCalendar.getRemindDate1() && matterCalendar.getRemindDate1().getTime()==nowDate.getTime().getTime()){
                if(matterCalendar.getType()==0 && matterCalendar.getTempId()>0){
                    //发行人事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getCompanyName().equals(matterCalendar.getCompanyName())
                                    && m.getTempId()==matterCalendar.getTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                            matterCalendarDate.setCalendarDate(matterCalendar.getRemindDate1());
                            matterCalendarDateList.add(matterCalendarDate);
                        }
                    }
                }else if(matterCalendar.getType()==0 && matterCalendar.getCustTempId()>0){
                    //发行人算定义事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getCompanyName().equals(matterCalendar.getCompanyName())
                                    && m.getCustTempId()==matterCalendar.getCustTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                            matterCalendarDate.setCalendarDate(matterCalendar.getRemindDate1());
                            matterCalendarDateList.add(matterCalendarDate);
                        }
                    }
                }else if(matterCalendar.getType()==1 && matterCalendar.getTempId()>0){
                    //项目事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getBondCode().equals(matterCalendar.getBondCode())
                                    && m.getTempId()==matterCalendar.getTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                            matterCalendarDate.setCalendarDate(matterCalendar.getRemindDate1());
                            matterCalendarDateList.add(matterCalendarDate);
                        }
                    }
                }else if(matterCalendar.getType()==1 && matterCalendar.getCustTempId()>0){
                    //项目自定义事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getBondCode().equals(matterCalendar.getBondCode())
                                    && m.getCustTempId()==matterCalendar.getCustTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()
                                    && m.getOrderIndex()==matterCalendar.getOrderIndex()
                    ).count()<=0){
                        if(matterCalendarDateList.stream().filter(m -> m.getCalendarDate().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                            MatterCalendarDate matterCalendarDate = new MatterCalendarDate();
                            matterCalendarDate.setCalendarDate(matterCalendar.getRemindDate1());
                            matterCalendarDateList.add(matterCalendarDate);
                        }
                    }
                }

            //}
        }

        return matterCalendarDateList;
    }

    @ResponseBody
    @RequestMapping(value = "/findCalendarDayRemind", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findCalendarDayRemind(@RequestBody(required = false) ReqData reqData){

        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        List<IsHoliday> isHolidayList = holidayService.findAll();

        String strDate = reqData.getString("remindDate");
        String status = reqData.getString("status");

        Date inDate = DateFormat.convert2Date(strDate, "yyyy-MM-dd");

        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(inDate);

        reqData.put("stime", strDate);
        reqData.put("etime", strDate);

        //未触发的事项
        List<MatterCalendar> matterCalendarList = matterCalendarService.findMonthRemindMatter(reqData);
        //记录最终数据
        List<MatterCalendar> dayCalendarList = new ArrayList<>();

        if(null!=matterCalendarList && matterCalendarList.size()>0){
            for(MatterCalendar matterCalendar : matterCalendarList){

                if(matterCalendar.getType()==0){
                    //发行人事项
                    //提醒时间
                    if(null!=matterCalendar.getRemindDate()){
                        Calendar stepDate = Calendar.getInstance();
                        stepDate.setTime(matterCalendar.getRemindDate());
                        int compStep =  nowDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                        boolean difFlag = true;
                        while(difFlag){
                            //Calendar stepDate = Calendar.getInstance();
                            //stepDate.setTime(matterCalendar.getRemindDate());
                            stepDate.add(Calendar.YEAR, compStep);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    if(maxDate.getTime() >= nowDate.getTime().getTime()){
                                        difFlag = false;
                                        matterCalendar.setRemindDate1(maxDate);
                                    }
                                }
                            }else{
                                if(stepDate.getTime().getTime() >= nowDate.getTime().getTime()){
                                    difFlag = false;
                                    matterCalendar.setRemindDate1(stepDate.getTime());
                                }
                            }
                            //计算完成日
                            if(!difFlag){
                                Calendar cDate = Calendar.getInstance();
                                cDate.setTime(matterCalendar.getCompleteDate());
                                cDate.set(Calendar.YEAR, stepDate.get(Calendar.YEAR));
                                if(cDate.getTime().getTime()<stepDate.getTime().getTime()){
                                    cDate.add(Calendar.YEAR, 1);
                                }
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==cDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=cDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=cDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        matterCalendar.setCompleteDate1(cMaxDate);
                                    }
                                }else{
                                    matterCalendar.setCompleteDate1(cDate.getTime());
                                }
                            }
                            compStep += 1;
                        }
                    }

                }else if(matterCalendar.getType()==1){
                    //项目事项
                    if(matterCalendar.getRuleType()==0){
                        if(null!=matterCalendar.getRemindDate()){
                            //按固定日期触发
                            Calendar stepDate = Calendar.getInstance();
                            stepDate.setTime(matterCalendar.getRemindDate());
                            int compStep =  nowDate.get(Calendar.YEAR) - stepDate.get(Calendar.YEAR);

                            boolean difFlag = true;
                            //int compStep = 0;
                            while(difFlag){
                                //Calendar stepDate = Calendar.getInstance();
                                //stepDate.setTime(matterCalendar.getRemindDate());
                                stepDate.add(Calendar.YEAR, compStep);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==stepDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=stepDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        //最终触发日期是否在有效日期内
                                        if(maxDate.getTime() >= matterCalendar.getValueDate().getTime() && maxDate.getTime() <= matterCalendar.getPayDay().getTime()) {
                                            if (maxDate.getTime() >= nowDate.getTime().getTime()) {
                                                difFlag = false;
                                                matterCalendar.setRemindDate1(maxDate);
                                            }
                                        }else{
                                            if(maxDate.getTime()>nowDate.getTime().getTime()){
                                                matterCalendar.setRemindDate1(null);
                                                difFlag = false;
                                            }
                                        }
                                    }
                                }else{
                                    //最终触发日期是否在有效日期内
                                    if(stepDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                        //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                        if(stepDate.getTime().getTime() >= nowDate.getTime().getTime()){
                                            matterCalendar.setRemindDate1(stepDate.getTime());
                                            difFlag = false;
                                        }
                                    }else{
                                        if(stepDate.getTime().getTime()>nowDate.getTime().getTime()){
                                            matterCalendar.setRemindDate1(null);
                                            difFlag = false;
                                        }
                                    }
                                }
                                //计算完成日
                                if(!difFlag){
                                    Calendar cDate = Calendar.getInstance();
                                    cDate.setTime(matterCalendar.getCompleteDate());
                                    cDate.set(Calendar.YEAR, stepDate.get(Calendar.YEAR));
                                    if(cDate.getTime().getTime()<stepDate.getTime().getTime()){
                                        cDate.add(Calendar.YEAR, 1);
                                    }
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==cDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=cDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=cDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            matterCalendar.setCompleteDate1(cMaxDate);
                                        }
                                    }else{
                                        matterCalendar.setCompleteDate1(cDate.getTime());
                                    }
                                }
                                compStep += 1;
                            }
                        }

                    }else if(matterCalendar.getRuleType()==1){
                        //按T-N触发
                        //提醒频率
                        if(matterCalendar.getPayFrequency()==0){
                            //按季度
                            boolean isFlag = true;
                            int step = 0;

                            //int valueDay = valueDate.get(Calendar.DATE);
                            while(isFlag){
                                //下个季度触发日期
                                Calendar nextRDate = Calendar.getInstance();
                                Calendar nextCDate = Calendar.getInstance();
                                nextRDate.setTime(matterCalendar.getValueDate());
                                nextRDate.add(Calendar.MONTH, step*3);
                                nextCDate.setTime(nextRDate.getTime());

                                int stepRemindBeforeDay = matterCalendar.getRemindBeforeDay();
                                while (stepRemindBeforeDay > 0){
                                    nextRDate.add(Calendar.DATE, -1);
                                    //节假日表里是否有设置日期
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            nextRDate.setTime(maxDate);
                                        }
                                    }
                                    stepRemindBeforeDay = stepRemindBeforeDay - 1;
                                }
                                //最终触发日期是否在有效日期内
                                if(nextRDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                    //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                    if(nextRDate.getTime().getTime() >= nowDate.getTime().getTime() && nextRDate.getTime().getTime() <= nowDate.getTime().getTime()){
                                        matterCalendar.setRemindDate1(nextRDate.getTime());
                                        isFlag = false;
                                    }
                                }else{
                                    if(nextRDate.getTime().getTime()>nowDate.getTime().getTime()){
                                        matterCalendar.setRemindDate1(null);
                                        isFlag = false;
                                    }
                                }
                                //计算完成日
                                if(!isFlag){
//                                    nextCDate.add(Calendar.DATE, -matterCalendar.getCompleteBeforeDay());
//                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
//                                        //节假日表里有效的工作日
//                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                        matterCalendar.setCompleteDate1(cMaxDate);
//                                    }else{
//                                        matterCalendar.setCompleteDate1(nextCDate.getTime());
//                                    }
                                    int stepCompleteBeforeDay = matterCalendar.getCompleteBeforeDay();
                                    while (stepCompleteBeforeDay > 0){
                                        nextCDate.add(Calendar.DATE, -1);
                                        //节假日表里是否有设置日期
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                                            //节假日表里有效的工作日
                                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                                Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                                nextCDate.setTime(cMaxDate);
                                            }
                                        }
                                        stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
                                    }
                                    matterCalendar.setCompleteDate1(nextCDate.getTime());
                                }
                                step += 1;
                            }
                        }else if(matterCalendar.getPayFrequency()==1){
                            //按年
                            boolean isFlag = true;
                            int step = 0;

                            //int valueDay = valueDate.get(Calendar.DATE);
                            while(isFlag){
                                //下个年度触发日期
                                Calendar nextCDate = Calendar.getInstance();
                                Calendar nextRDate = Calendar.getInstance();
                                nextRDate.setTime(matterCalendar.getValueDate());
                                nextRDate.add(Calendar.YEAR, step);
                                nextCDate.setTime(nextRDate.getTime());

                                int stepRemindBeforeDay = matterCalendar.getRemindBeforeDay();
                                while (stepRemindBeforeDay > 0){
                                    nextRDate.add(Calendar.DATE, -1);
                                    //节假日表里是否有设置日期
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            nextRDate.setTime(maxDate);
                                        }
                                    }
                                    stepRemindBeforeDay = stepRemindBeforeDay - 1;
                                }
                                //最终触发日期是否在有效日期内
                                if(nextRDate.getTime().getTime() <= matterCalendar.getPayDay().getTime()){
                                    //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
                                    if(nextRDate.getTime().getTime() >= nowDate.getTime().getTime()){
                                        matterCalendar.setRemindDate1(nextRDate.getTime());
                                        isFlag = false;
                                    }
                                }else{
                                    if(nextRDate.getTime().getTime()>nowDate.getTime().getTime()){
                                        matterCalendar.setRemindDate1(null);
                                        isFlag = false;
                                    }
                                }
                                //计算完成日
                                if(!isFlag){
//                                    nextCDate.add(Calendar.DATE, -matterCalendar.getCompleteBeforeDay());
//                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
//                                        //节假日表里有效的工作日
//                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                        matterCalendar.setCompleteDate1(cMaxDate);
//                                    }else{
//                                        matterCalendar.setCompleteDate1(nextCDate.getTime());
//                                    }
                                    int stepCompleteBeforeDay = matterCalendar.getCompleteBeforeDay();
                                    while (stepCompleteBeforeDay > 0){
                                        nextCDate.add(Calendar.DATE, -1);
                                        //节假日表里是否有设置日期
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                                            //节假日表里有效的工作日
                                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                                Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                                nextCDate.setTime(cMaxDate);
                                            }
                                        }
                                        stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
                                    }
                                    matterCalendar.setCompleteDate1(nextCDate.getTime());
                                }
                                step += 1;
                            }
                        }
                    }
                }
            }
        }


        //************************************************
        //已触发的事项
        List<MatterCalendar> processCalendarList = matterCalendarService.findProcessDay(reqData);
        for(MatterCalendar matterCalendar : matterCalendarList){
            if(null!=matterCalendar.getRemindDate1() && matterCalendar.getRemindDate1().getTime()==nowDate.getTime().getTime()){
                if(matterCalendar.getType()==0 && matterCalendar.getTempId()>0){
                    //发行人事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getCompanyName().equals(matterCalendar.getCompanyName())
                                    && m.getTempId()==matterCalendar.getTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }else if(matterCalendar.getType()==0 && matterCalendar.getCustTempId()>0){
                    //发行人算定义事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getCompanyName().equals(matterCalendar.getCompanyName())
                                    && m.getCustTempId()==matterCalendar.getCustTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }else if(matterCalendar.getType()==1 && matterCalendar.getTempId()>0){
                    //项目事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getBondCode().equals(matterCalendar.getBondCode())
                                    && m.getTempId()==matterCalendar.getTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }else if(matterCalendar.getType()==1 && matterCalendar.getCustTempId()>0){
                    //项目自定义事项
                    if(processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                                    && m.getBondCode().equals(matterCalendar.getBondCode())
                                    && m.getCustTempId()==matterCalendar.getCustTempId()
                                    && m.getRuleId()==matterCalendar.getRuleId()
                                    && m.getRemindDate1().getTime()==matterCalendar.getRemindDate1().getTime()
                                    && m.getOrderIndex()==matterCalendar.getOrderIndex()
                            ).count()<=0){
                        dayCalendarList.add(matterCalendar);
                    }
                }

            }
        }


        List<MatterCalendarGroup> matterCalendarGroupList = new ArrayList<>();

        //模板事项
        Map<Integer, List<MatterCalendar>> matterCalendarMap = dayCalendarList.stream().filter(m -> m.getCustTempId()<=0).collect(Collectors.groupingBy(e -> e.getTempId()));
        Iterator it = matterCalendarMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<MatterCalendar>> mc = (Map.Entry) it.next();

            MatterCalendarGroup matterCalendarGroup = new MatterCalendarGroup();
            matterCalendarGroup.setId(mc.getValue().get(0).getTempId());
            matterCalendarGroup.setType(mc.getValue().get(0).getType());
            matterCalendarGroup.setName(mc.getValue().get(0).getTempName());
            matterCalendarGroup.setShortname(mc.getValue().get(0).getTempShortname());
            matterCalendarGroup.setDescription(mc.getValue().get(0).getTempDescription());
            matterCalendarGroup.setColor(mc.getValue().get(0).getNowTempColor());
            matterCalendarGroup.setMatterCalendarList(mc.getValue());
            matterCalendarGroupList.add(matterCalendarGroup);
        }

        //自定义模板事项
        dayCalendarList.stream().forEach(calendar -> {
            if(calendar.getCustTempId()>0){
                MatterCalendarGroup matterCalendarGroup = new MatterCalendarGroup();
                matterCalendarGroup.setId(calendar.getTempId());
                matterCalendarGroup.setType(3);
                matterCalendarGroup.setName(calendar.getTempName());
                matterCalendarGroup.setShortname(calendar.getTempShortname());
                matterCalendarGroup.setDescription(calendar.getTempDescription());
                matterCalendarGroup.setColor(calendar.getNowTempColor());
                List<MatterCalendar> itemMatter = new ArrayList<MatterCalendar>();
                itemMatter.add(calendar);
                matterCalendarGroup.setMatterCalendarList(itemMatter);
                matterCalendarGroupList.add(matterCalendarGroup);
            }
        });


        int pageCount = 0;
        if(null!=matterCalendarGroupList && matterCalendarGroupList.size()>0){
            pageCount = matterCalendarGroupList.size();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", pageCount);

        Integer pageNum = 0;
        Integer pageSize = 10;
        if (reqData.getInteger("pNum") != null) {
            pageNum = reqData.getInteger("pNum");
        }
        if (reqData.getInteger("pSize") != null) {
            pageSize = reqData.getInteger("pSize");
        }
        pageNum = pageNum * pageSize;
        //pageSize = pageNum + pageSize;
        List<MatterCalendarGroup> rows = matterCalendarGroupList.stream()
                //.sorted((x,y) -> {return null==x.getCompleteDate1()?-1:(x.getCompleteDate1().getTime()>=y.getCompleteDate1().getTime() ? -1 : 1);})
                .skip(pageNum).limit(pageSize).collect(Collectors.toList());
        map.put("rows", rows);

        return map;
    }


    private boolean isExist(MatterCalendar matterCalendar, List<MatterCalendar> processCalendarList, Date date){
        boolean returnFlag = false;
        if(null==processCalendarList || processCalendarList.size()<=0){
            return true;
        }
        if(matterCalendar.getType()==0 && matterCalendar.getTempId()>0){
            //发行人事项
            returnFlag = processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                            && m.getCompanyName().equals(matterCalendar.getCompanyName())
                            && m.getTempId()==matterCalendar.getTempId()
                            && m.getRuleId()==matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==date.getTime()
            ).count()<=0;
        }else if(matterCalendar.getType()==0 && matterCalendar.getCustTempId()>0){
            //发行人算定义事项
            returnFlag = processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                            && m.getCompanyName().equals(matterCalendar.getCompanyName())
                            && m.getCustTempId()==matterCalendar.getCustTempId()
                            && m.getRuleId()==matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==date.getTime()
            ).count()<=0;
        }else if(matterCalendar.getType()==1 && matterCalendar.getTempId()>0){
            //项目事项
            returnFlag = processCalendarList.stream().filter(m ->
                            m.getType()==matterCalendar.getType()
                            && m.getBondCode().equals(matterCalendar.getBondCode())
                            && m.getTempId()==matterCalendar.getTempId()
                            && m.getRuleId()==matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==date.getTime()
            ).count()<=0;
        }else if(matterCalendar.getType()==1 && matterCalendar.getCustTempId()>0) {
            //项目自定义事项
            returnFlag = processCalendarList.stream().filter(m ->
                    m.getType() == matterCalendar.getType()
                            && m.getBondCode().equals(matterCalendar.getBondCode())
                            && m.getCustTempId() == matterCalendar.getCustTempId()
                            && m.getRuleId() == matterCalendar.getRuleId()
                            && m.getCompleteDate1().getTime()==date.getTime()
                            && m.getOrderIndex() == matterCalendar.getOrderIndex()
            ).count() <= 0;
        }
        return returnFlag;
    }
}
