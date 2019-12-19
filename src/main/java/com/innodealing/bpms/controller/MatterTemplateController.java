package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.innodealing.bpms.appconfig.history.CompareObject;
import com.innodealing.bpms.appconfig.history.Operate;
import com.innodealing.bpms.appconfig.history.commap.BondMap;
import com.innodealing.bpms.common.model.HistoryLog;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.*;
import com.innodealing.bpms.unit.DateFormat;
import com.innodealing.commons.http.RestResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mattertemplate")
public class MatterTemplateController {

    @Autowired
    MatterTemplateService matterTemplateService;
    @Autowired
    CompanyService companyService;
    @Autowired
    BondService bondService;
    @Autowired
    UserService userService;
    @Autowired
    HolidayService holidayService;
    @Autowired
    HistoryService historyService;

    private static final Logger logger = LoggerFactory.getLogger(MatterTemplateController.class);
    private final static String OrgViewPrefix = "app/bpms/mattertemplate/";

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model){
        List<MatterTemplate> matterTemplateList = matterTemplateService.findAll();
        List<MatterTemplate> orgTemplateList = matterTemplateList.stream().filter(a -> a.getType()==0).collect(Collectors.toList());
        List<MatterTemplate> proTemplateList = matterTemplateList.stream().filter(a -> a.getType()==1).collect(Collectors.toList());
        model.addAttribute("orgTemplateList", orgTemplateList);
        model.addAttribute("proTemplateList", proTemplateList);
        return OrgViewPrefix + "index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model){
        return OrgViewPrefix + "add";
    }

    //保存
    @Operate(mark="新增事项", type=1, operateType=1)
    @ResponseBody
    @RequestMapping("/addSave")
    public RestResponse<Integer> addSave(@RequestBody MatterTemplate matterTemplate) {
        RestResponse<Integer> result;

        try {
            Subject subject = SecurityUtils.getSubject();
            String id = (String) subject.getPrincipals().getPrimaryPrincipal();
            matterTemplate.setUserId(id);
            matterTemplate.setId(0);
            if(matterTemplateService.isExist(matterTemplate)>0){
                result = RestResponse.Fail("添加失败：该模板名称已经存在",-1);
            }else{
                int count = matterTemplateService.insertMatterTemplate(matterTemplate);
                if(count > 0){
                    result = RestResponse.Success(matterTemplate.getId());
                }else{
                    result = RestResponse.Fail("添加失败",-1);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("添加失败",-1);
        }

        return result;
    }

    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public String info(Model model, @PathVariable Integer id){
        model.addAttribute("templateId", id);
        return OrgViewPrefix + "info";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable Integer id){
        MatterTemplate matterTemplate = matterTemplateService.findById(id);
        model.addAttribute("matterTemplate", matterTemplate);
        return OrgViewPrefix + "edit";
    }

    @RequestMapping(value = "/companylink/{id}", method = RequestMethod.GET)
    public String companylink(Model model, @PathVariable int id){
        List<Company> companyList = companyService.findLinkMatterCount(id);
        int unLinkCount = companyList.stream().filter(company -> null==company.getMatterId()).collect(Collectors.toList()).size();
        int linkCount = 0;
        List<Company> linkCompanyList = companyList.stream().filter(company -> null!=company.getMatterId()).collect(Collectors.toList());
        StringBuffer sb = new StringBuffer();
        for(Company company : linkCompanyList){
            linkCount += 1;
            if(linkCount==linkCompanyList.size()){
                sb.append(company.getName());
            }else{
                sb.append(company.getName()+",");
            }
        }
        model.addAttribute("unLinkCount", unLinkCount);
        model.addAttribute("linkCount", linkCount);
        model.addAttribute("templateId", id);
        model.addAttribute("linkNames", sb.toString());
        return OrgViewPrefix + "companylink";
    }

    @RequestMapping(value = "/companylinkinfo/{id}", method = RequestMethod.GET)
    public String companylinkinfo(Model model, @PathVariable int id){
        model.addAttribute("templateId", id);
        return OrgViewPrefix + "companylinkinfo";
    }

    @RequestMapping(value = "/bondlinkinfo/{id}", method = RequestMethod.GET)
    public String bondlinkinfo(Model model, @PathVariable int id){
        model.addAttribute("templateId", id);
        return OrgViewPrefix + "bondlinkinfo";
    }

    @RequestMapping(value = "/bondlink/{id}", method = RequestMethod.GET)
    public String bondlink(Model model, @PathVariable int id){
        List<Bond> bondList = bondService.findLinkMatterCount(id);
        int unLinkCount = bondList.stream().filter(bond -> null==bond.getMatterId()).collect(Collectors.toList()).size();
        int linkCount = 0;
        List<Bond> linkBondList = bondList.stream().filter(bond -> null!=bond.getMatterId()).collect(Collectors.toList());
        StringBuffer sb = new StringBuffer();
        for(Bond bond : linkBondList){
            linkCount += 1;
            if(linkCount==linkBondList.size()){
                sb.append(bond.getCode());
            }else{
                sb.append(bond.getCode()+",");
            }
        }
        model.addAttribute("unLinkCount", unLinkCount);
        model.addAttribute("linkCount", linkCount);
        model.addAttribute("templateId", id);
        model.addAttribute("linkNames", sb.toString());
        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        return OrgViewPrefix + "bondlink";
    }

    @ResponseBody
    @RequestMapping(value = "/findCompanyLinkMatterAll", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findCompanyLinkMatterAll(@RequestBody(required = false) ReqData reqData){
//        PageInfo<Company> page = companyService.findLinkMatterAll(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("total", page.getTotal());
//        map.put("rows", page.getList());

        int templateId = reqData.getInteger("templateId").intValue();
        List<Company> companyList = companyService.findLinkMatterCount(templateId);
        int unLinkCount = companyList.stream().filter(company -> null==company.getMatterId()).collect(Collectors.toList()).size();
        int linkCount = companyList.stream().filter(company -> null!=company.getMatterId()).collect(Collectors.toList()).size();
        map.put("unLinkCount", unLinkCount);
        map.put("linkCount", linkCount);

        Integer pageNum = 0;
        Integer pageSize = 10;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        //pageNum = pageNum * pageSize;
        //pageSize = pageNum + pageSize;

        List<Company> companyListAll = companyService.findCompLinkAll(reqData);
        int total = companyListAll.size();
        List<Company> rows = companyListAll.stream().skip(pageNum).limit(pageSize).collect(Collectors.toList());

        map.put("total", total);
        map.put("rows", rows);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/findBondLinkMatterAll", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findBondLinkMatterAll(@RequestBody(required = false) ReqData reqData){
        PageInfo<Bond> page = bondService.findLinkMatterAll(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());

        int templateId = reqData.getInteger("templateId").intValue();
        List<Bond> bondList = bondService.findLinkMatterCount(templateId);
        int unLinkCount = bondList.stream().filter(bond -> null==bond.getMatterId()).collect(Collectors.toList()).size();
        int linkCount = bondList.stream().filter(bond -> null!=bond.getMatterId()).collect(Collectors.toList()).size();
        map.put("unLinkCount", unLinkCount);
        map.put("linkCount", linkCount);
        return map;
    }

    //编辑保存
    @Operate(mark="修改事项", type = 1, operateType = 2)
    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<Integer> editSave(@RequestBody MatterTemplate matterTemplate) {
        RestResponse<Integer> result;

        try {
            Subject subject = SecurityUtils.getSubject();
            String id = (String) subject.getPrincipals().getPrimaryPrincipal();
            matterTemplate.setUserId(id);
            if(matterTemplateService.isExist(matterTemplate)>0){
                result = RestResponse.Fail("更新失败:该模板名称已经存在",matterTemplate.getId());
            }else{
                int count = matterTemplateService.updateMatterTemplate(matterTemplate);
                if(count > 0){
                    result = RestResponse.Success(matterTemplate.getId());
                }else{
                    result = RestResponse.Fail("更新失败",matterTemplate.getId());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("更新失败",matterTemplate.getId());
        }

        return result;
    }

    @Operate(mark="删除事项", type = 1, operateType = 3)
    @ResponseBody
    @RequestMapping(value = "/deleteSave", method = RequestMethod.POST)
    public RestResponse<String> deleteSave(@RequestBody Integer id){
        RestResponse<String> result;
        try{
            if(null!=id){
                int resultStatus = matterTemplateService.deleteMatterTemplate(id);
                if(resultStatus>0){
                    result = RestResponse.Success( "删除成功", "");
                }else{
                    result = RestResponse.Fail("删除失败","");
                }
            }else{
                logger.info("删除失败");
                result = RestResponse.Fail("删除失败","");
            }
        }catch(Exception ex){
            logger.info("删除失败:" + ex.getMessage());
            result = RestResponse.Fail("删除失败","");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/findByTemplateId/{id}", method = RequestMethod.GET)
    public MatterTemplate findByTemplateId(@PathVariable int id){
        //假期表
        List<IsHoliday> isHolidayList = holidayService.findAll();

        MatterTemplate matterTemplate = matterTemplateService.findByTemplateId(id);

        int mailBeforeDay = matterTemplate.getMailBeforeDay();
        Date mailBeforeDate = findMailCompleteDate(matterTemplate.getTemplateRuleList(), isHolidayList, mailBeforeDay);
        matterTemplate.setMailBeforeDate(mailBeforeDate);
        return matterTemplate;
    }

    @ResponseBody
    @RequestMapping(value = "/findBondByTemplateId", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public MatterTemplate findBondByTemplateId(@RequestBody(required = false) ReqData reqData){
        int id = reqData.getInteger("id").intValue();
        int payFrequency = reqData.getInteger("payFrequency").intValue();
        MatterTemplate matterTemplate = matterTemplateService.findByTemplateId(id);
        //假期表
        List<IsHoliday> isHolidayList = holidayService.findAll();
        if(matterTemplate.getType()==1 && null!=matterTemplate.getTemplateRuleList() && matterTemplate.getTemplateRuleList().get(0).getType()==1){
            //系统当前日期
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date nowDate = DateFormat.convert2Date(df.format(date), "yyyy-MM-dd");

            //项目付息日
            String strValueDate = reqData.getString("valueDate");
            Date valueDate = DateFormat.convert2Date(strValueDate, "yyyy-MM-dd");
            //项目有效日
            String strPayDay = reqData.getString("payDay");
            Date payDay = DateFormat.convert2Date(strPayDay, "yyyy-MM-dd");

            for(TemplateRule templateRule : matterTemplate.getTemplateRuleList()){
                if(templateRule.getType()==1){
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
                            Calendar mailCDate = Calendar.getInstance();
                            nextRDate.setTime(remindDate.getTime());
                            nextRDate.add(Calendar.MONTH, step*3);
                            nextCDate.setTime(nextRDate.getTime());
                            mailCDate.setTime(nextRDate.getTime());

                            int stepBeforeDay = templateRule.getBeforeDay();
                            while (stepBeforeDay>0){
                                nextRDate.add(Calendar.DATE, -1);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                        Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                        nextRDate.setTime(maxDate);
                                    }
                                }
                                stepBeforeDay = stepBeforeDay - 1;
                            }
                            //最终触发日期是否在有效日期内
                            if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                                templateRule.setRemindDate(nextRDate.getTime());
                                isFlag = false;
                            }
                            //计算完成日
                            if(!isFlag){

                                int stepCompleteBeforeDay = templateRule.getCompleteBeforeDay();
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
                                templateRule.setCompleteDate(nextCDate.getTime());

                                //记算邮件提醒提前日期
                                int stepMailBeforeDay = templateRule.getCompleteBeforeDay() + matterTemplate.getMailBeforeDay();
                                while (stepMailBeforeDay>0){
                                    mailCDate.add(Calendar.DATE, -1);
                                    //节假日表里是否有设置日期
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            mailCDate.setTime(cMaxDate);
                                        }
                                    }
                                    stepMailBeforeDay = stepMailBeforeDay - 1;
                                }
                                matterTemplate.setMailBeforeDate(mailCDate.getTime());
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
                            Calendar nextCDate = Calendar.getInstance();
                            Calendar nextRDate = Calendar.getInstance();
                            Calendar mailCDate = Calendar.getInstance();
                            nextRDate.setTime(remindDate.getTime());
                            nextRDate.add(Calendar.YEAR, step);
                            nextCDate.setTime(nextRDate.getTime());
                            mailCDate.setTime(nextRDate.getTime());

                            int stepBeforeDay = templateRule.getBeforeDay();
                            while (stepBeforeDay>0){
                                nextRDate.add(Calendar.DATE, -1);
                                //节假日表里是否有设置日期
                                if(isHolidayList.stream().filter(m -> m.getDate().getTime()==nextRDate.getTime().getTime()).count()>0){
                                    //节假日表里有效的工作日
                                    Date maxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=nextRDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                    nextRDate.setTime(maxDate);
                                }
                                stepBeforeDay = stepBeforeDay - 1;
                            }
                            //最终触发日期是否在有效日期内
                            if(nextRDate.getTime().getTime() >= nowDate.getTime()){
                                templateRule.setRemindDate(nextRDate.getTime());
                                isFlag = false;
                            }
                            //计算完成日
                            if(!isFlag){

                                int stepCompleteBeforeDay = templateRule.getCompleteBeforeDay();
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
                                templateRule.setCompleteDate(nextCDate.getTime());

                                //记算邮件提醒提前日期
                                int stepMailBeforeDay = templateRule.getCompleteBeforeDay() + matterTemplate.getMailBeforeDay();
                                while (stepMailBeforeDay>0){
                                    mailCDate.add(Calendar.DATE, -1);
                                    //节假日表里是否有设置日期
                                    if(isHolidayList.stream().filter(m -> m.getDate().getTime()==mailCDate.getTime().getTime()).count()>0){
                                        //节假日表里有效的工作日
                                        if(isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).count()>0){
                                            Date cMaxDate = isHolidayList.stream().filter(m -> m.getDate().getTime()<=mailCDate.getTime().getTime() && m.getIsHoliday()==0).max((a,b)->a.getDate().compareTo(b.getDate())).get().getDate();
                                            mailCDate.setTime(cMaxDate);
                                        }
                                    }
                                    stepMailBeforeDay = stepMailBeforeDay - 1;
                                }
                                matterTemplate.setMailBeforeDate(mailCDate.getTime());
                            }
                            step += 1;
                        }
                    }
                }
            }
        }else if(matterTemplate.getType()==1 && null!=matterTemplate.getTemplateRuleList() && matterTemplate.getTemplateRuleList().get(0).getType()==0){
            Date mailBeforeDate = findMailCompleteDate(matterTemplate.getTemplateRuleList(), isHolidayList, matterTemplate.getMailBeforeDay());
            matterTemplate.setMailBeforeDate(mailBeforeDate);
        }

        return matterTemplate;
    }

    private Date findMailCompleteDate(List<TemplateRule> templateRuleList, List<IsHoliday> holidayList, int mailBeforDay){
        Calendar nowDate = Calendar.getInstance();
        String strNowDate = DateFormat.convert2String(nowDate.getTime(), "yyyy-MM-dd");
        Date dateNow = DateFormat.convert2Date(strNowDate, "yyyy-MM-dd");
        nowDate.setTime(dateNow);

        Calendar resultDate = Calendar.getInstance();
        resultDate.add(Calendar.YEAR, 5);
        for(TemplateRule templateRule : templateRuleList){
            if(null!=templateRule.getCompleteDate()){
                //计算出当前年日期
                Calendar completeDate = Calendar.getInstance();
                completeDate.setTime(templateRule.getCompleteDate());
                String strCompleteDate = DateFormat.convert2String(completeDate.getTime(), "yyyy-MM-dd");
                Date dateCompleteDate = DateFormat.convert2Date(strCompleteDate, "yyyy-MM-dd");
                completeDate.setTime(dateCompleteDate);

                completeDate.add(Calendar.YEAR, nowDate.get(Calendar.YEAR)-completeDate.get(Calendar.YEAR));
                if(null!=templateRule.getRemindDate()){
                    Calendar remindDate = Calendar.getInstance();
                    remindDate.setTime(templateRule.getRemindDate());
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
                    completeDate.add(Calendar.DATE, -mailBeforDay);
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
                    }
                }
            }

        }
        return resultDate.getTime();
    }


    @RequestMapping(value = "/history/{id}", method = RequestMethod.GET)
    public String history(Model model, @PathVariable Integer id) throws Exception{
        ReqData reqData = new ReqData();
        reqData.put("type", 1);
        reqData.put("templateId", id);
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){
            History oldHistory = historyList.get(0);
            for (History history: historyList) {
                HistoryLog historyLog = new HistoryLog();
                historyLog.setLogDate(history.getCreateTime());
                historyLog.setLogUserId(history.getUserId());
                historyLog.setLogUser(history.getUserName());
                historyLog.setLogMark(history.getMark());
                historyLog.setLogOperateType(history.getOperateType());

                MatterTemplate oldMatterTemplate = null;
                MatterTemplate nowMatterTemplate = null;

                Gson gson = new Gson();
                JsonParser parser = new JsonParser();

                JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                if(null!=oldAarray && oldAarray.size()>0){
                    oldMatterTemplate = gson.fromJson(oldAarray.get(0), MatterTemplate.class);
                }

                JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                if(null!=nowAarray && nowAarray.size()>0){
                    nowMatterTemplate = gson.fromJson(nowAarray.get(0), MatterTemplate.class);
                }

                List<String> contentList = CompareObject.compare(oldMatterTemplate, nowMatterTemplate);
                historyLog.setLogContent(contentList);
                historyLogList.add(historyLog);
                oldHistory = history;
            }
        }

        model.addAttribute("historyLogList",
                historyLogList.stream()
                .filter(m->!m.getLogUserId().equals("000000"))
                .sorted(Comparator.comparing(HistoryLog::getLogDate).reversed()).collect(Collectors.toList()));
        return OrgViewPrefix + "history";
    }

    @RequestMapping(value = "/companylinkhistory/{id}", method = RequestMethod.GET)
    public String companylinkhistory(Model model, @PathVariable Integer id) throws Exception{
        ReqData reqData = new ReqData();
        reqData.put("type", 101);
        reqData.put("templateId", id);
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){
            History oldHistory = new History();
            int index = 0;
            for (History history: historyList) {
                HistoryLog historyLog = new HistoryLog();
                if(history.getOperateType()==3){
                    //取消所有关联
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(2);

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray jsonArray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                    if(null!=jsonArray && jsonArray.size()>0){
                        List<CompanyMatter> companyMatterList = gson.fromJson(jsonArray.get(0), new TypeToken<List<CompanyMatter>>() {}.getType());
                        List<String> logContent = new ArrayList<>();
                        for(CompanyMatter companyMatter : companyMatterList){
                            logContent.add("<span style='font-weight: bold;'>" + companyMatter.getCompanyName() + "</span>：由 “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                        }
                        historyLog.setLogContent(logContent);
                    }
                }else if(index==0){
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(history.getOperateType());

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray jsonArray = parser.parse(history.getArgs()).getAsJsonArray();
                    if(null!=jsonArray && jsonArray.size()>0){
                        List<CompanyMatter> companyMatterList = gson.fromJson(jsonArray.get(0), new TypeToken<List<CompanyMatter>>() {}.getType());
                        List<String> logContent = new ArrayList<>();
                        for(CompanyMatter companyMatter : companyMatterList){
                            logContent.add("<span style='font-weight: bold;'>" + companyMatter.getCompanyName() + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                        }
                        historyLog.setLogContent(logContent);
                    }
                }else{
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(history.getOperateType());

                    List<Object> oldCompanyMatterList = new ArrayList<>();
                    List<Object> nowCompanyMatterList = new ArrayList<>();

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                    if(null!=oldAarray && oldAarray.size()>0){
                        oldCompanyMatterList = gson.fromJson(oldAarray.get(0), new TypeToken<List<CompanyMatter>>() {}.getType());
                    }

                    JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                    if(null!=nowAarray && nowAarray.size()>0){
                        nowCompanyMatterList = gson.fromJson(nowAarray.get(0), new TypeToken<List<CompanyMatter>>() {}.getType());
                    }

                    List<String> contentList = CompareObject.compareCompanyList(oldCompanyMatterList, nowCompanyMatterList);
                    historyLog.setLogContent(contentList);
                }
                historyLogList.add(historyLog);
                oldHistory = history;
                index++;
            }

        }

        model.addAttribute("historyLogList", historyLogList.stream()
                .filter(m->!m.getLogUserId().equals("000000"))
                .sorted(Comparator.comparing(HistoryLog::getLogDate).reversed()).collect(Collectors.toList()));
        return OrgViewPrefix + "history";
    }

    @RequestMapping(value = "/bondlinkhistory/{id}", method = RequestMethod.GET)
    public String bondlinkhistory(Model model, @PathVariable Integer id) throws Exception{
        ReqData reqData = new ReqData();
        reqData.put("type", 101);
        reqData.put("templateId", id);
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){
            BondMap.refreshMap();

            List<Bond> bondList = bondService.findAllBond();

            History oldHistory = new History();
            int index = 0;
            for (History history: historyList) {
                HistoryLog historyLog = new HistoryLog();
                if(history.getOperateType()==3){
                    //取消所有关联
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(2);

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray jsonArray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                    if(null!=jsonArray && jsonArray.size()>0){
                        List<BondMatter> bondMatterList = gson.fromJson(jsonArray.get(0), new TypeToken<List<BondMatter>>() {}.getType());
                        List<String> logContent = new ArrayList<>();
                        for(BondMatter bondMatter : bondMatterList){
                            Bond bond = bondList.stream().filter(m -> m.getCode().equals(bondMatter.getBondCode())).findFirst().get();
                            if(null!=bond && null!=bond.getShortname()){
                                logContent.add("<span style='font-weight: bold;'>" + bond.getShortname() + "</span>：由 “勾选” <span style='font-weight: bold;'>修改为</span> “未勾选”");
                            }
                        }
                        historyLog.setLogContent(logContent);
                    }
                }else if(index==0){
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(history.getOperateType());

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray jsonArray = parser.parse(history.getArgs()).getAsJsonArray();
                    if(null!=jsonArray && jsonArray.size()>0){
                        List<BondMatter> bondMatterList = gson.fromJson(jsonArray.get(0), new TypeToken<List<BondMatter>>() {}.getType());
                        List<String> logContent = new ArrayList<>();
                        for(BondMatter bondMatter : bondMatterList){
                            Bond bond = bondList.stream().filter(m -> m.getCode().equals(bondMatter.getBondCode())).findFirst().get();
                            if(null!=bond && null!=bond.getShortname()){
                                logContent.add("<span style='font-weight: bold;'>" + bond.getShortname() + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                            }
                        }
                        historyLog.setLogContent(logContent);
                    }
                }else{
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(history.getOperateType());

                    List<Object> oldBondMatterList = new ArrayList<>();
                    List<Object> nowBondMatterList = new ArrayList<>();

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                    if(null!=oldAarray && oldAarray.size()>0){
                        oldBondMatterList = gson.fromJson(oldAarray.get(0), new TypeToken<List<BondMatter>>() {}.getType());
                    }

                    JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                    if(null!=nowAarray && nowAarray.size()>0){
                        nowBondMatterList = gson.fromJson(nowAarray.get(0), new TypeToken<List<BondMatter>>() {}.getType());
                    }

                    List<String> contentList = CompareObject.compareBondList(oldBondMatterList, nowBondMatterList);
                    historyLog.setLogContent(contentList);
                }
                historyLogList.add(historyLog);
                oldHistory = history;
                index++;
            }
        }

        model.addAttribute("historyLogList",
                historyLogList.stream()
                .filter(m->!m.getLogUserId().equals("000000"))
                .sorted(Comparator.comparing(HistoryLog::getLogDate).reversed()).collect(Collectors.toList()));
        return OrgViewPrefix + "history";
    }
}
