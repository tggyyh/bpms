package com.innodealing.bpms.controller;

import com.innodealing.bpms.appconfig.history.Operate;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.BondService;
import com.innodealing.bpms.service.CustomMatterService;
import com.innodealing.bpms.service.HolidayService;
import com.innodealing.bpms.unit.DateFormat;
import com.innodealing.commons.http.RestResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/custommatter")
public class CustomMatterController {

    @Autowired
    CustomMatterService customMatterService;
    @Autowired
    HolidayService holidayService;
    @Autowired
    BondService bondService;

    private static final Logger logger = LoggerFactory.getLogger(CustomMatterController.class);
    private final static String OrgViewPrefix = "app/bpms/custommatter/";


    @RequestMapping(value = "/add/{matterType}/{matterKey:.+}", method = RequestMethod.GET)
    public String add(Model model, @PathVariable int matterType, @PathVariable String matterKey){
        model.addAttribute("matterType", matterType);
        model.addAttribute("matterKey", matterKey);
        return OrgViewPrefix + "add";
    }

    //保存
    @Operate(mark="新增自定义事项", type = 4, operateType = 1)
    @ResponseBody
    @RequestMapping("/addSave")
    public RestResponse<History> addSave(@RequestBody CustomMatter customMatter) {
        RestResponse<History> result;

        try {
            int count = customMatterService.insertCustomMatter(customMatter);
            if(count > 0){
                History history = new History();
                history.setCustomId(customMatter.getId());
                if(customMatter.getType()==0){
                    history.setCompanyName(customMatter.getKey());
                }else if(customMatter.getType()==1){
                    history.setBondCode(customMatter.getKey());
                }
                result = RestResponse.Success(String.valueOf(customMatter.getId()), history);

            }else{
                result = RestResponse.Fail("添加失败", null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("添加失败", null);
        }

        return result;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable int id){
        CustomMatter customMatter = customMatterService.findById(id);
        if(null!=customMatter && customMatter.getType()==1 && null!= customMatter.getCustomRuleList()){
            //假期表
            List<IsHoliday> isHolidayList = holidayService.findAll();
            if(customMatter.getCustomRuleList().get(0).getType()==1){
                Bond bond = bondService.findBondByCode(customMatter.getKey());
                if(customMatter.getRightLine()==0){
                    setCustomRuleDate(customMatter, bond, isHolidayList);
                }else if(customMatter.getRightLine()==1){
                    setCustomSubRuleDate(customMatter, bond, isHolidayList);
                }
            }else if(customMatter.getCustomRuleList().get(0).getType()==0){
                setCustomMailDate(customMatter, isHolidayList);
            }

        }
        model.addAttribute("customMatter", customMatter);
        return OrgViewPrefix + "edit";
    }

    //保存
    @Operate(mark="修改自定义事项",type = 4, operateType = 2)
    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<History> editSave(@RequestBody CustomMatter customMatter) {
        RestResponse<History> result;

        try {
            int count = customMatterService.updateCustomMatter(customMatter);
            if(count > 0){
                History history = new History();
                history.setCustomId(customMatter.getId());
                if(customMatter.getType()==0){
                    history.setCompanyName(customMatter.getKey());
                }else if(customMatter.getType()==1){
                    history.setBondCode(customMatter.getKey());
                }
                result = RestResponse.Success("", history);
            }else{
                result = RestResponse.Fail("更新失败", null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("更新失败", null);
        }

        return result;
    }

    @Operate(mark="删除自定义事项",type = 4, operateType = 3)
    @ResponseBody
    @RequestMapping(value = "/deleteSave", method = RequestMethod.POST)
    public RestResponse<History> deleteSave(@RequestBody CustomMatter customMatter){
        RestResponse<History> result;
        try{
            if(null!=customMatter){
                int resultStatus = customMatterService.deleteCustomMatter(customMatter.getId());
                if(resultStatus>0){
                    History history = new History();
                    history.setCustomId(customMatter.getId());
                    if(customMatter.getType()==0){
                        history.setCompanyName(customMatter.getKey());
                    }else if(customMatter.getType()==1){
                        history.setBondCode(customMatter.getKey());
                    }
                    result = RestResponse.Success( "删除成功", history);
                }else{
                    result = RestResponse.Fail("删除失败", null);
                }
            }else{
                logger.info("删除失败");
                result = RestResponse.Fail("删除失败", null);
            }
        }catch(Exception ex){
            logger.info("删除失败:" + ex.getMessage());
            result = RestResponse.Fail("删除失败", null);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    public CustomMatter findById(@PathVariable int id){
        CustomMatter customMatter = customMatterService.findById(id);

        //假期表
        List<IsHoliday> isHolidayList = holidayService.findAll();
        getCustomSub(customMatter, isHolidayList, 0, new Date());
        return customMatter;
    }

    @ResponseBody
    @RequestMapping(value = "/findBondById", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public CustomMatter findBondById(@RequestBody(required = false) ReqData reqData){
        int id = reqData.getInteger("id").intValue();
        int payFrequency = reqData.getInteger("payFrequency").intValue();
        //项目付息日
        String strValueDate = reqData.getString("valueDate");
        Date valueDate = DateFormat.convert2Date(strValueDate, "yyyy-MM-dd");
        //项目有效日
        String strPayDay = reqData.getString("payDay");
        Date payDay = DateFormat.convert2Date(strPayDay, "yyyy-MM-dd");

        CustomMatter customMatter = customMatterService.findById(id);


        if(null!=customMatter && customMatter.getType()==1 && null!= customMatter.getCustomRuleList()){
            //假期表
            List<IsHoliday> isHolidayList = holidayService.findAll();
            if(customMatter.getCustomRuleList().get(0).getType()==1){
                Bond bond = new Bond();
                bond.setPayFrequency(payFrequency);
                bond.setValueDate(valueDate);
                bond.setPayDay(payDay);

                if(customMatter.getRightLine()==0){
                    setCustomRuleDate(customMatter, bond, isHolidayList);
                }else if(customMatter.getRightLine()==1){
                    setCustomSubRuleDate(customMatter, bond, isHolidayList);
                }
            }else if(customMatter.getCustomRuleList().get(0).getType()==0){
                setCustomMailDate(customMatter, isHolidayList);
            }

        }


        //假期表
//        List<IsHoliday> isHolidayList = holidayService.findAll();
//        if(customMatter.getType()==1 && null!=customMatter.getCustomRuleList() && customMatter.getCustomRuleList().get(0).getType()==1){
//            //系统当前日期
//            Date date = new Date();
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");
//
//
//
//            for(CustomRule customRule : customMatter.getCustomRuleList()){
//                if(customRule.getType()==1){
//                    //提醒时间
//                    Calendar remindDate = Calendar.getInstance();
//                    remindDate.setTime(valueDate);
//
//                    if(payFrequency==0){
//                        //按季度
//                        boolean isFlag = true;
//                        int step = 0;
//                        while(isFlag){
//                            //下个季度触发日期
//                            Calendar nextRDate = Calendar.getInstance();
//                            Calendar nextCDate = Calendar.getInstance();
//                            Calendar mailCDate = Calendar.getInstance();
//                            nextRDate.setTime(remindDate.getTime());
//                            nextRDate.add(Calendar.MONTH, step*3);
//                            nextCDate.setTime(nextRDate.getTime());
//                            mailCDate.setTime(nextRDate.getTime());
//
//                            int stepBeforeDay = customRule.getBeforeDay();
//                            while (stepBeforeDay>0){
//                                nextRDate.add(Calendar.DATE, -1);
//                                //节假日表里是否有设置日期
//                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
//                                    //节假日表里有效的工作日
//                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                    nextRDate.setTime(maxDate);
//                                }
//                                stepBeforeDay = stepBeforeDay - 1;
//                            }
//                            //最终触发日期是否在有效日期内
//                            if(nextRDate.getTime().getTime() >= nowDate.getTime()){
//                                customRule.setRemindDate(nextRDate.getTime());
//                                isFlag = false;
//                            }
//                            //计算完成日
//                            if(!isFlag){
//
//                                int stepCompleteBeforeDay = customRule.getCompleteBeforeDay();
//                                while (stepCompleteBeforeDay>0){
//                                    nextCDate.add(Calendar.DATE, -1);
//                                    //节假日表里是否有设置日期
//                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
//                                        //节假日表里有效的工作日
//                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                        nextCDate.setTime(cMaxDate);
//                                    }
//                                    stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
//                                }
//                                customRule.setCompleteDate(nextCDate.getTime());
//
//                                int mailCompleteBeforeDay = customRule.getCompleteBeforeDay() + customMatter.getMailBeforeDay();
//                                while (mailCompleteBeforeDay>0){
//                                    mailCDate.add(Calendar.DATE, -1);
//                                    //节假日表里是否有设置日期
//                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
//                                        //节假日表里有效的工作日
//                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                        mailCDate.setTime(cMaxDate);
//                                    }
//                                    mailCompleteBeforeDay = mailCompleteBeforeDay - 1;
//                                }
//                                customMatter.setMailBeforeDate(mailCDate.getTime());
//                            }
//                            step += 1;
//                        }
//                    }else if(payFrequency==1){
//                        //按年
//                        boolean isFlag = true;
//                        int step = 0;
//
//                        //int valueDay = valueDate.get(Calendar.DATE);
//                        while(isFlag){
//                            //下个年度触发日期
//                            Calendar nextCDate = Calendar.getInstance();
//                            Calendar nextRDate = Calendar.getInstance();
//                            Calendar mailCDate = Calendar.getInstance();
//                            nextRDate.setTime(remindDate.getTime());
//                            nextRDate.add(Calendar.YEAR, step);
//                            nextCDate.setTime(nextRDate.getTime());
//                            mailCDate.setTime(nextRDate.getTime());
//
//                            int stepBeforeDay = customRule.getBeforeDay();
//                            while (stepBeforeDay>0){
//                                nextRDate.add(Calendar.DATE, -1);
//                                //节假日表里是否有设置日期
//                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
//                                    //节假日表里有效的工作日
//                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                    nextRDate.setTime(maxDate);
//                                }
//                                stepBeforeDay = stepBeforeDay - 1;
//                            }
//                            //最终触发日期是否在有效日期内
//                            if(nextRDate.getTime().getTime() >= nowDate.getTime()){
//                                customRule.setRemindDate(nextRDate.getTime());
//                                isFlag = false;
//                            }
//
//                            //计算完成日
//                            if(!isFlag){
//
//                                int stepCompleteBeforeDay = customRule.getCompleteBeforeDay();
//                                while (stepCompleteBeforeDay>0){
//                                    nextCDate.add(Calendar.DATE, -1);
//                                    //节假日表里是否有设置日期
//                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
//                                        //节假日表里有效的工作日
//                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                        nextCDate.setTime(cMaxDate);
//                                    }
//                                    stepCompleteBeforeDay = stepCompleteBeforeDay - 1;
//                                }
//                                customRule.setCompleteDate(nextCDate.getTime());
//
//                                int mailCompleteBeforeDay = customRule.getCompleteBeforeDay() + customMatter.getMailBeforeDay();
//                                while (mailCompleteBeforeDay>0){
//                                    mailCDate.add(Calendar.DATE, -1);
//                                    //节假日表里是否有设置日期
//                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
//                                        //节假日表里有效的工作日
//                                        Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                                        mailCDate.setTime(cMaxDate);
//                                    }
//                                    mailCompleteBeforeDay = mailCompleteBeforeDay - 1;
//                                }
//                                customMatter.setMailBeforeDate(mailCDate.getTime());
//                            }
//                            step += 1;
//                        }
//                    }
//                }
//            }
//        }
//
//        //邮箱提醒日期
//        getCustomSub(customMatter, isHolidayList, payFrequency, valueDate);
        return customMatter;
    }

    //邮箱提醒日期
    private void getCustomSub(CustomMatter customMatter, List<IsHoliday> isHolidayList, int payFrequency, Date valueDate){
        //系统当前日期
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");

        //非行权付息
        if(customMatter.getRightLine()==0 && null!=customMatter.getCustomRuleList() && customMatter.getCustomRuleList().size()>0){
            if(customMatter.getCustomRuleList().get(0).getType()==0){
                //固定时间
                Calendar canNowDate = Calendar.getInstance();
                canNowDate.setTime(nowDate);

                int mailBeforDay = customMatter.getMailBeforeDay();

                Calendar resultDate = Calendar.getInstance();
                resultDate.add(Calendar.YEAR, 5);
                for(CustomRule customRule : customMatter.getCustomRuleList()){
                    if(null!=customRule.getCompleteDate()){
                        //计算出当前年日期
                        Calendar completeDate = Calendar.getInstance();
                        completeDate.setTime(customRule.getCompleteDate());
                        String strCompleteDate = DateFormat.convert2String(completeDate.getTime(), "yyyy-MM-dd");
                        Date dateCompleteDate = DateFormat.convert2Date(strCompleteDate, "yyyy-MM-dd");
                        completeDate.setTime(dateCompleteDate);

                        completeDate.add(Calendar.YEAR, canNowDate.get(Calendar.YEAR)-completeDate.get(Calendar.YEAR));
                        if(null!=customRule.getRemindDate()){
                            Calendar remindDate = Calendar.getInstance();
                            remindDate.setTime(customRule.getRemindDate());
                            remindDate.add(Calendar.YEAR, canNowDate.get(Calendar.YEAR)-remindDate.get(Calendar.YEAR));
                            String strRemindDate = DateFormat.convert2String(remindDate.getTime(), "yyyy-MM-dd");
                            Date dateRemindDate = DateFormat.convert2Date(strRemindDate, "yyyy-MM-dd");
                            remindDate.setTime(dateRemindDate);

                            //需完成时间小于提醒时间，说明需完成时间是跨年
                            if(remindDate.getTime().getTime()>completeDate.getTime().getTime()){
                                completeDate.add(Calendar.YEAR, 1);
                            }
                        }

                        boolean isFlag = true;
                        int step = 0;
                        while(isFlag){
                            completeDate.add(Calendar.YEAR, step);
                            //计算出提前天数
                            completeDate.add(Calendar.DATE, -mailBeforDay);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==completeDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=completeDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=completeDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    if(maxDate.getTime() >= canNowDate.getTime().getTime()){
                                        completeDate.setTime(maxDate);
                                        isFlag = false;
                                    }
                                }
                            }else{
                                if(completeDate.getTime().getTime() >= canNowDate.getTime().getTime()){
                                    isFlag = false;
                                }
                            }
                            step +=1 ;
                        }
                        if(!isFlag){
                            if(completeDate.getTime().getTime()<=resultDate.getTime().getTime()){
                                customMatter.setMailBeforeDate(completeDate.getTime());
                            }
                        }
                    }
                }

            }else if(customMatter.getCustomRuleList().get(0).getType()==1){
                //T-N
                //提醒时间
                Calendar remindDate = Calendar.getInstance();
                remindDate.setTime(valueDate);

                if(payFrequency==0){
                    //按季度
                    boolean isFlag = true;
                    int step = 0;
                    while(isFlag){
                        //下个季度触发日期
                        Calendar mailCDate = Calendar.getInstance();
                        mailCDate.setTime(remindDate.getTime());
                        mailCDate.add(Calendar.MONTH, step*3);

                        int stepBeforeDay = customMatter.getCustomRuleList().get(0).getCompleteBeforeDay() + customMatter.getMailBeforeDay();
                        while (stepBeforeDay>0){
                            mailCDate.add(Calendar.DATE, -1);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    mailCDate.setTime(maxDate);
                                }
                            }
                            stepBeforeDay = stepBeforeDay - 1;
                        }
                        //最终触发日期是否在有效日期内
                        if(mailCDate.getTime().getTime() >= nowDate.getTime()){
                            customMatter.setMailBeforeDate(mailCDate.getTime());
                            isFlag = false;
                        }

                        step += 1;
                    }
                }else if(payFrequency==1){
                    //按年
                    boolean isFlag = true;
                    int step = 0;

                    //int valueDay = valueDate.get(Calendar.DATE);
                    while(isFlag){
                        //下个年度触发日期
                        Calendar mailCDate = Calendar.getInstance();
                        mailCDate.setTime(remindDate.getTime());
                        mailCDate.add(Calendar.YEAR, step);

                        int stepBeforeDay = customMatter.getCustomRuleList().get(0).getCompleteBeforeDay() + customMatter.getMailBeforeDay();
                        while (stepBeforeDay>0){
                            mailCDate.add(Calendar.DATE, -1);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    mailCDate.setTime(maxDate);
                                }
                            }
                            stepBeforeDay = stepBeforeDay - 1;
                        }
                        //最终触发日期是否在有效日期内
                        if(mailCDate.getTime().getTime() >= nowDate.getTime()){
                            customMatter.setMailBeforeDate(mailCDate.getTime());
                            isFlag = false;
                        }
                        step += 1;
                    }
                }
            }
        }else if(customMatter.getRightLine()==1){
            //行权付息
            for(CustomSubMatter customSubMatter : customMatter.getCustomSubMatterList()){
                //提醒时间
                Calendar remindDate = Calendar.getInstance();
                remindDate.setTime(valueDate);

                if(payFrequency==0){
                    //按季度
                    boolean isFlag = true;
                    int step = 0;
                    while(isFlag){
                        //下个季度触发日期
                        Calendar mailCDate = Calendar.getInstance();
                        mailCDate.setTime(remindDate.getTime());
                        mailCDate.add(Calendar.MONTH, step*3);

                        int stepBeforeDay = customMatter.getCustomRuleList().get(0).getCompleteBeforeDay() + customSubMatter.getMailBeforeDay();
                        while (stepBeforeDay>0){
                            mailCDate.add(Calendar.DATE, -1);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    mailCDate.setTime(maxDate);
                                }
                            }
                            stepBeforeDay = stepBeforeDay - 1;
                        }
                        //最终触发日期是否在有效日期内
                        if(mailCDate.getTime().getTime() >= nowDate.getTime()){
                            customSubMatter.setMailBeforeDate(mailCDate.getTime());
                            isFlag = false;
                        }

                        step += 1;
                    }
                }else if(payFrequency==1){
                    //按年
                    boolean isFlag = true;
                    int step = 0;

                    //int valueDay = valueDate.get(Calendar.DATE);
                    while(isFlag){
                        //下个年度触发日期
                        Calendar mailCDate = Calendar.getInstance();
                        mailCDate.setTime(remindDate.getTime());
                        mailCDate.add(Calendar.YEAR, step);

                        int stepBeforeDay = customMatter.getCustomRuleList().get(0).getCompleteBeforeDay() + customSubMatter.getMailBeforeDay();
                        while (stepBeforeDay>0){
                            mailCDate.add(Calendar.DATE, -1);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    mailCDate.setTime(maxDate);
                                }
                            }
                            stepBeforeDay = stepBeforeDay - 1;
                        }
                        //最终触发日期是否在有效日期内
                        if(mailCDate.getTime().getTime() >= nowDate.getTime()){
                            customSubMatter.setMailBeforeDate(mailCDate.getTime());
                            isFlag = false;
                        }
                        step += 1;
                    }
                }
            }

        }
    }

    @ResponseBody
    @RequestMapping(value = "/findByKey/{type}/{key:.+}", method = RequestMethod.GET)
    public List<CustomMatter> findByKey(@PathVariable int type, @PathVariable String key){
        CustomMatter customMatter = new CustomMatter();
        customMatter.setType(type);
        customMatter.setKey(key);
        customMatter.setStatus(0);
        List<CustomMatter> customMatterList = customMatterService.findByKey(customMatter);
        return customMatterList;
    }

    @ResponseBody
    @RequestMapping(value = "/findRemindDate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String findRemindDate(@RequestBody(required = false) ReqData reqData){
        String returnDate = "";

        String bondCode = reqData.getString("bondCode");
        Bond bond = bondService.findBondByCode(bondCode);

        int payFrequency = bond.getPayFrequency().intValue();

        //系统当前日期
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");
        //假期表
        List<IsHoliday> isHolidayList = holidayService.findAll();
        //项目付息日
        Date valueDate = bond.getValueDate();
        //提前天数
        int beforeDay = reqData.getInteger("beforeDay").intValue();

        //提醒时间
        Calendar remindDate = Calendar.getInstance();
        remindDate.setTime(valueDate);

        if(payFrequency==0){
            //按季度
            boolean isFlag = true;
            int step = 0;
            while(isFlag){
                //下个季度触发日期
                Calendar nextRDate = Calendar.getInstance();
                Calendar nextCDate = Calendar.getInstance();
                nextRDate.setTime(remindDate.getTime());
                nextRDate.add(Calendar.MONTH, step*3);
                nextCDate.setTime(nextRDate.getTime());

                //提前天数
//                nextRDate.add(Calendar.DATE, -beforeDay);
//                //节假日表里是否有设置日期
//                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
//                    //节假日表里有效的工作日
//                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                    if(maxDate.getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(maxDate, "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }else{
//                    //最终触发日期是否在有效日期内
//                    if(nextCDate.getTime().getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(nextCDate.getTime(), "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }
                int stepBeoreDay = beforeDay;
                while (stepBeoreDay>0){
                    nextRDate.add(Calendar.DATE, -1);
                    //节假日表里是否有设置日期
                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            nextRDate.setTime(maxDate);
                        }
                    }
                    stepBeoreDay = stepBeoreDay - 1;
                }
                if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                    returnDate = DateFormat.convert2String(nextRDate.getTime(), "yyyy-MM-dd");
                    isFlag = false;
                }

                step += 1;
            }
        }else if(payFrequency==1){
            //按年
            boolean isFlag = true;
            int step = 0;

            while(isFlag){
                //下个年度触发日期
                Calendar nextRDate = Calendar.getInstance();
                nextRDate.setTime(remindDate.getTime());
                nextRDate.add(Calendar.YEAR, step);

                //提前天数
//                nextRDate.add(Calendar.DATE, -beforeDay);
//                //节假日表里是否有设置日期
//                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
//                    //节假日表里有效的工作日
//                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                    //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
//                    if(maxDate.getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(maxDate, "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }else{
//                    //最终触发日期是否在有效日期内
//                    if(nextRDate.getTime().getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(nextRDate.getTime(), "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }
                int stepBeoreDay = beforeDay;
                while (stepBeoreDay>0){
                    nextRDate.add(Calendar.DATE, -1);
                    //节假日表里是否有设置日期
                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            nextRDate.setTime(maxDate);
                        }
                    }
                    stepBeoreDay = stepBeoreDay - 1;
                }
                if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                    returnDate = DateFormat.convert2String(nextRDate.getTime(), "yyyy-MM-dd");
                    isFlag = false;
                }
                step += 1;
            }
        }
        return returnDate;
    }

    @ResponseBody
    @RequestMapping(value = "/findCompleteDate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String findCompleteDate(@RequestBody(required = false) ReqData reqData){
        String returnDate = "";

        String bondCode = reqData.getString("bondCode");
        Bond bond = bondService.findBondByCode(bondCode);

        int payFrequency = bond.getPayFrequency().intValue();

        //系统当前日期
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");
        //假期表
        List<IsHoliday> isHolidayList = holidayService.findAll();
        //项目付息日
        Date valueDate = bond.getValueDate();
        //提前天数
        int completeDay = reqData.getInteger("completeDay").intValue();

        //提醒时间
        Calendar completeDate = Calendar.getInstance();
        completeDate.setTime(valueDate);

        if(payFrequency==0){
            //按季度
            boolean isFlag = true;
            int step = 0;
            while(isFlag){
                //下个季度触发日期
                Calendar nextCDate = Calendar.getInstance();
                nextCDate.setTime(completeDate.getTime());
                nextCDate.add(Calendar.MONTH, step*3);

                //提前天数
//                nextCDate.add(Calendar.DATE, -completeDay);
//                //节假日表里是否有设置日期
//                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
//                    //节假日表里有效的工作日
//                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                    if(maxDate.getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(maxDate, "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }else{
//                    //最终触发日期是否在有效日期内
//                    if(nextCDate.getTime().getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(nextCDate.getTime(), "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }
                int stepCompleteDay = completeDay;
                while (stepCompleteDay>0){
                    nextCDate.add(Calendar.DATE, -1);
                    //节假日表里是否有设置日期
                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            nextCDate.setTime(maxDate);
                        }
                    }
                    stepCompleteDay = stepCompleteDay - 1;
                }
                if(nextCDate.getTime().getTime() >= nowDate.getTime()){
                    returnDate = DateFormat.convert2String(nextCDate.getTime(), "yyyy-MM-dd");
                    isFlag = false;
                }
                step += 1;
            }
        }else if(payFrequency==1){
            //按年
            boolean isFlag = true;
            int step = 0;

            while(isFlag){
                //下个年度触发日期
                Calendar nextCDate = Calendar.getInstance();
                nextCDate.setTime(completeDate.getTime());
                nextCDate.add(Calendar.YEAR, step);

                //提前天数
//                nextCDate.add(Calendar.DATE, -completeDay);
//                //节假日表里是否有设置日期
//                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
//                    //节假日表里有效的工作日
//                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
//                    //最终触发日期是否在查询时间范围内，在范围内记录触发日期，结束流程
//                    if(maxDate.getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(maxDate, "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }else{
//                    //最终触发日期是否在有效日期内
//                    if(nextCDate.getTime().getTime() >= nowDate.getTime()){
//                        returnDate = DateFormat.convert2String(nextCDate.getTime(), "yyyy-MM-dd");
//                        isFlag = false;
//                    }
//                }
                int stepCompleteDay = completeDay;
                while (stepCompleteDay>0){
                    nextCDate.add(Calendar.DATE, -1);
                    //节假日表里是否有设置日期
                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            nextCDate.setTime(maxDate);
                        }
                    }
                    stepCompleteDay = stepCompleteDay - 1;
                }
                if(nextCDate.getTime().getTime() >= nowDate.getTime()){
                    returnDate = DateFormat.convert2String(nextCDate.getTime(), "yyyy-MM-dd");
                    isFlag = false;
                }
                step += 1;
            }
        }
        return returnDate;
    }

    @ResponseBody
    @RequestMapping(value = "/findMailCompleteDate", method = RequestMethod.POST)
    public String findMailCompleteDate(@RequestBody List<RuleDate> ruleDateList){
        String returnDate = "";

        List<IsHoliday> holidayList = holidayService.findAll();

        Calendar nowDate = Calendar.getInstance();
        String strNowDate = DateFormat.convert2String(nowDate.getTime(), "yyyy-MM-dd");
        Date dateNow = DateFormat.convert2Date(strNowDate, "yyyy-MM-dd");
        nowDate.setTime(dateNow);

        Calendar resultDate = Calendar.getInstance();
        resultDate.add(Calendar.YEAR, 5);
        for(RuleDate ruleDate : ruleDateList){
            if(null!=ruleDate.getCompleteDate()){
                //计算出当前年日期
                Calendar completeDate = Calendar.getInstance();
                completeDate.setTime(ruleDate.getCompleteDate());
                String strCompleteDate = DateFormat.convert2String(completeDate.getTime(), "yyyy-MM-dd");
                Date dateCompleteDate = DateFormat.convert2Date(strCompleteDate, "yyyy-MM-dd");
                completeDate.setTime(dateCompleteDate);

                completeDate.add(Calendar.YEAR, nowDate.get(Calendar.YEAR)-completeDate.get(Calendar.YEAR));
                if(null!=ruleDate.getRemindDate()){
                    Calendar remindDate = Calendar.getInstance();
                    remindDate.setTime(ruleDate.getRemindDate());
                    remindDate.add(Calendar.YEAR, nowDate.get(Calendar.YEAR)-remindDate.get(Calendar.YEAR));
                    String strRemindDate = DateFormat.convert2String(remindDate.getTime(), "yyyy-MM-dd");
                    Date dateRemindDate = DateFormat.convert2Date(strRemindDate, "yyyy-MM-dd");
                    remindDate.setTime(dateRemindDate);

                    //需完成时间小于提醒时间，说明需完成时间是跨年
                    if(remindDate.getTime().getTime()>completeDate.getTime().getTime()){
                        completeDate.add(Calendar.YEAR, 1);
                    }
                }

                boolean isFlag = true;
                int step = 0;
                while(isFlag){
                    completeDate.add(Calendar.YEAR, step);
                    //计算出提前天数
                    completeDate.add(Calendar.DATE, -ruleDate.getBeforeDay());
                    //节假日表里是否有设置日期
                    if(holidayList.stream().filter(m -> m.getDate().getTime()==completeDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(holidayList.stream().filter(m -> m.getDate().getTime()<=completeDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = holidayList.stream().filter(m -> m.getDate().getTime()<=completeDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            if(maxDate.getTime() >= nowDate.getTime().getTime()){
                                completeDate.setTime(maxDate);
                                isFlag = false;
                            }
                        }

                    }else{
                        if(completeDate.getTime().getTime() >= nowDate.getTime().getTime()){
                            isFlag = false;
                        }
                    }
                    step +=1 ;
                }
                if(!isFlag){
                    if(completeDate.getTime().getTime()<=resultDate.getTime().getTime()){
                        resultDate.setTime(completeDate.getTime());
                        returnDate = DateFormat.convert2String(resultDate.getTime(), "yyyy-MM-dd");
                    }
                }
            }

        }
        return returnDate;
    }

    //自定义固定规则日期
    private void setCustomMailDate(CustomMatter customMatter, List<IsHoliday> isHolidayList){
        //系统当前日期
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");

        //非行权付息
        if(customMatter.getRightLine()==0 && null!=customMatter.getCustomRuleList() && customMatter.getCustomRuleList().size()>0){
            if(customMatter.getCustomRuleList().get(0).getType()==0){
                //固定时间
                Calendar canNowDate = Calendar.getInstance();
                canNowDate.setTime(nowDate);

                int mailBeforDay = customMatter.getMailBeforeDay();

                Calendar resultDate = Calendar.getInstance();
                resultDate.add(Calendar.YEAR, 5);
                for(CustomRule customRule : customMatter.getCustomRuleList()){
                    if(null!=customRule.getCompleteDate()){
                        //计算出当前年日期
                        Calendar completeDate = Calendar.getInstance();
                        completeDate.setTime(customRule.getCompleteDate());
                        String strCompleteDate = DateFormat.convert2String(completeDate.getTime(), "yyyy-MM-dd");
                        Date dateCompleteDate = DateFormat.convert2Date(strCompleteDate, "yyyy-MM-dd");
                        completeDate.setTime(dateCompleteDate);

                        completeDate.add(Calendar.YEAR, canNowDate.get(Calendar.YEAR)-completeDate.get(Calendar.YEAR));
                        if(null!=customRule.getRemindDate()){
                            Calendar remindDate = Calendar.getInstance();
                            remindDate.setTime(customRule.getRemindDate());
                            remindDate.add(Calendar.YEAR, canNowDate.get(Calendar.YEAR)-remindDate.get(Calendar.YEAR));
                            String strRemindDate = DateFormat.convert2String(remindDate.getTime(), "yyyy-MM-dd");
                            Date dateRemindDate = DateFormat.convert2Date(strRemindDate, "yyyy-MM-dd");
                            remindDate.setTime(dateRemindDate);

                            //需完成时间小于提醒时间，说明需完成时间是跨年
                            if(remindDate.getTime().getTime()>completeDate.getTime().getTime()){
                                completeDate.add(Calendar.YEAR, 1);
                            }
                        }

                        boolean isFlag = true;
                        int step = 0;
                        while(isFlag){
                            completeDate.add(Calendar.YEAR, step);
                            //计算出提前天数
                            completeDate.add(Calendar.DATE, -mailBeforDay);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==completeDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=completeDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=completeDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    if(maxDate.getTime() >= canNowDate.getTime().getTime()){
                                        completeDate.setTime(maxDate);
                                        isFlag = false;
                                    }
                                }
                            }else{
                                if(completeDate.getTime().getTime() >= canNowDate.getTime().getTime()){
                                    isFlag = false;
                                }
                            }
                            step +=1 ;
                        }
                        if(!isFlag){
                            if(completeDate.getTime().getTime()<=resultDate.getTime().getTime()){
                                customMatter.setMailBeforeDate(completeDate.getTime());
                            }
                        }
                    }
                }

            }
        }
    }
    //自定义T-非行权规则日期
    private void setCustomRuleDate(CustomMatter customMatter, Bond bond, List<IsHoliday> isHolidayList){
        int payFrequency = bond.getPayFrequency().intValue();

        //系统当前日期
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");
        //假期表
        //List<IsHoliday> isHolidayList = holidayService.findAll();
        //项目付息日
        Date valueDate = bond.getValueDate();

        //提前天数
        int beforeDay = customMatter.getCustomRuleList().get(0).getBeforeDay();
        int completeBeforeDay = customMatter.getCustomRuleList().get(0).getCompleteBeforeDay();
        int mailBeforeDay = customMatter.getMailBeforeDay();

        //提醒时间
        Calendar remindDate = Calendar.getInstance();
        remindDate.setTime(valueDate);

        if(payFrequency==0){
            //按季度
            boolean isFlag = true;
            int step = 0;
            while(isFlag){
                //下个季度触发日期
                Calendar nextRDate = Calendar.getInstance();
                Calendar nextCDate = Calendar.getInstance();
                nextRDate.setTime(remindDate.getTime());
                nextRDate.add(Calendar.MONTH, step*3);
                nextCDate.setTime(nextRDate.getTime());

                int stepBeoreDay = beforeDay;
                while (stepBeoreDay>0){
                    nextRDate.add(Calendar.DATE, -1);
                    //节假日表里是否有设置日期
                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            nextRDate.setTime(maxDate);
                        }
                    }
                    stepBeoreDay = stepBeoreDay - 1;
                }
                if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                    customMatter.getCustomRuleList().get(0).setRemindDate(nextRDate.getTime());
                    isFlag = false;
                }


                //计算完成日
                if(!isFlag){
                    int stepCompleteBeforeDay = completeBeforeDay;
                    while (stepCompleteBeforeDay>0){
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
                    customMatter.getCustomRuleList().get(0).setCompleteDate(nextCDate.getTime());

                    //计算提醒频率
                    int stepMailBeforeDay = mailBeforeDay;
                    while (stepMailBeforeDay>0){
                        nextCDate.add(Calendar.DATE, -1);
                        //节假日表里是否有设置日期
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                            //节假日表里有效的工作日
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                nextCDate.setTime(cMaxDate);
                            }
                        }
                        stepMailBeforeDay = stepMailBeforeDay - 1;
                    }
                    customMatter.setMailBeforeDate(nextCDate.getTime());

                }
                step += 1;
            }
        }else if(payFrequency==1){
            //按年
            boolean isFlag = true;
            int step = 0;

            while(isFlag){
                //下个年度触发日期
                Calendar nextRDate = Calendar.getInstance();
                Calendar nextCDate = Calendar.getInstance();
                nextRDate.setTime(remindDate.getTime());
                nextRDate.add(Calendar.YEAR, step);
                nextCDate.setTime(nextRDate.getTime());

                int stepBeoreDay = beforeDay;
                while (stepBeoreDay>0){
                    nextRDate.add(Calendar.DATE, -1);
                    //节假日表里是否有设置日期
                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                        //节假日表里有效的工作日
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                            Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                            nextRDate.setTime(maxDate);
                        }
                    }
                    stepBeoreDay = stepBeoreDay - 1;
                }
                if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                    customMatter.getCustomRuleList().get(0).setRemindDate(nextRDate.getTime());
                    isFlag = false;
                }

                //计算完成日
                if(!isFlag){
                    int stepCompleteBeforeDay = completeBeforeDay;
                    while (stepCompleteBeforeDay>0){
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
                    customMatter.getCustomRuleList().get(0).setCompleteDate(nextCDate.getTime());

                    //计算提醒频率
                    int stepMailBeforeDay = mailBeforeDay;
                    while (stepMailBeforeDay>0){
                        nextCDate.add(Calendar.DATE, -1);
                        //节假日表里是否有设置日期
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                            //节假日表里有效的工作日
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                nextCDate.setTime(cMaxDate);
                            }
                        }
                        stepMailBeforeDay = stepMailBeforeDay - 1;
                    }
                    customMatter.setMailBeforeDate(nextCDate.getTime());
                }
                step += 1;
            }
        }
    }
    //自定义T-行权附息规则日期
    private void setCustomSubRuleDate(CustomMatter customMatter, Bond bond, List<IsHoliday> isHolidayList){
        int payFrequency = bond.getPayFrequency().intValue();

        //系统当前日期
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");
        //假期表
        //List<IsHoliday> isHolidayList = holidayService.findAll();
        //项目付息日
        Date valueDate = bond.getValueDate();

        for(CustomSubMatter customSubMatter : customMatter.getCustomSubMatterList()){
            //提前天数
            int beforeDay = customSubMatter.getBeforeDay();
            int completeBeforeDay = customSubMatter.getCompleteBeforeDay();
            int mailBeforeDay = customSubMatter.getMailBeforeDay();

            //提醒时间
            Calendar remindDate = Calendar.getInstance();
            remindDate.setTime(valueDate);

            if(payFrequency==0){
                //按季度
                boolean isFlag = true;
                int step = 0;
                while(isFlag){
                    //下个季度触发日期
                    Calendar nextRDate = Calendar.getInstance();
                    Calendar nextCDate = Calendar.getInstance();
                    nextRDate.setTime(remindDate.getTime());
                    nextRDate.add(Calendar.MONTH, step*3);
                    nextCDate.setTime(nextRDate.getTime());

                    int stepBeoreDay = beforeDay;
                    while (stepBeoreDay>0){
                        nextRDate.add(Calendar.DATE, -1);
                        //节假日表里是否有设置日期
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                            //节假日表里有效的工作日
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                nextRDate.setTime(maxDate);
                            }
                        }
                        stepBeoreDay = stepBeoreDay - 1;
                    }
                    if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                        customSubMatter.setBeforeDate(nextRDate.getTime());
                        isFlag = false;
                    }

                    //计算完成日
                    if(!isFlag){
                        int stepCompleteBeforeDay = completeBeforeDay;
                        while (stepCompleteBeforeDay>0){
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
                        customSubMatter.setCompleteBeforeDate(nextCDate.getTime());

                        //计算提醒频率
                        int stepMailBeforeDay = mailBeforeDay;
                        while(stepMailBeforeDay>0){
                            nextCDate.add(Calendar.DATE, -1);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    nextCDate.setTime(cMaxDate);
                                }
                            }
                            stepMailBeforeDay = stepMailBeforeDay - 1;
                        }
                        customSubMatter.setMailBeforeDate(nextCDate.getTime());
                    }

                    step += 1;
                }
            }else if(payFrequency==1){
                //按年
                boolean isFlag = true;
                int step = 0;

                while(isFlag){
                    //下个年度触发日期
                    Calendar nextRDate = Calendar.getInstance();
                    Calendar nextCDate = Calendar.getInstance();
                    nextRDate.setTime(remindDate.getTime());
                    nextRDate.add(Calendar.YEAR, step);
                    nextCDate.setTime(nextRDate.getTime());

                    int stepBeoreDay = beforeDay;
                    while (stepBeoreDay>0){
                        nextRDate.add(Calendar.DATE, -1);
                        //节假日表里是否有设置日期
                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                            //节假日表里有效的工作日
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                nextRDate.setTime(maxDate);
                            }
                        }
                        stepBeoreDay = stepBeoreDay - 1;
                    }
                    if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                        customSubMatter.setBeforeDate(nextRDate.getTime());
                        isFlag = false;
                    }

                    //计算完成日
                    if(!isFlag){
                        int stepCompleteBeforeDay = completeBeforeDay;
                        while (stepCompleteBeforeDay>0){
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
                        customSubMatter.setCompleteBeforeDate(nextCDate.getTime());

                        //计算提醒频率日期
                        int stepMailBeforeDay = mailBeforeDay;
                        while (stepMailBeforeDay>0){
                            nextCDate.add(Calendar.DATE, -1);
                            //节假日表里是否有设置日期
                            if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextCDate.getTime().getTime()).count()>0){
                                //节假日表里有效的工作日
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                    Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    nextCDate.setTime(cMaxDate);
                                }
                            }
                            stepMailBeforeDay = stepMailBeforeDay - 1;
                        }
                        customSubMatter.setMailBeforeDate(nextCDate.getTime());
                    }
                    step += 1;
                }
            }
        }


    }
}
