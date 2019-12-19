package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.innodealing.bpms.appconfig.history.CompareObject;
import com.innodealing.bpms.appconfig.history.Operate;
import com.innodealing.bpms.appconfig.history.commap.CustomMatterMap;
import com.innodealing.bpms.appconfig.history.commap.MatterTemplateMap;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.HistoryLog;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.*;
import com.innodealing.commons.http.RestResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyMatterService companyMatterService;
    @Autowired
    private MatterTemplateService matterTemplateService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ProcessInfoService processInfoService;
    @Autowired
    private CustomMatterService customMatterService;
    @Autowired
    private DocumentFileService documentFileService;
    @Autowired
    private  UserService userService;
    @Autowired
    HistoryService historyService;

    private static final Logger logger = LoggerFactory.getLogger(MatterTemplateController.class);
    private final static String VIEW_PREFIX = "app/bpms/company/";

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        return VIEW_PREFIX + "index";
    }

    @ResponseBody
    @RequestMapping(value = "/findArea", method = RequestMethod.GET)
    public List<Area> findArea(){
        List<Area> areaList = areaService.findAll();
        return areaList;
    }

    @ResponseBody
    @RequestMapping(value = "/findAll", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findAll(@RequestBody(required = false) ReqData reqData){
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        User user = userService.findById(id);
        String roleCode = "", roleName = "";
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        PageInfo<Company> page = companyService.findAll(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());
        return map;
    }

    @Operate(mark="删除发行人", type = 3, operateType = 3)
    @ResponseBody
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RestResponse<String> updateStatus(@RequestBody(required = false) ReqData reqData){
        RestResponse<String> result;
        try{
            int resultStatus = companyService.updateStatus(reqData);
            if(resultStatus>0){
                result = RestResponse.Success(reqData.getString("companyName"));
            }else{
                result = RestResponse.Fail("删除失败","");
            }
        }catch(Exception ex){
            logger.info("删除失败:" + ex.getMessage());
            result = RestResponse.Fail("删除失败","");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteSave", method = RequestMethod.POST)
    public RestResponse<String> deleteSave(@RequestBody int[] ids){
        RestResponse<String> result;
        try{
            if(null!=ids && ids.length>0){
                int resultStatus = companyService.updateStatusByIds(ids);
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

    @RequestMapping(value = "/linkmatterInfo/{matterType}/{companyName:.+}", method = RequestMethod.GET)
    public String linkmatterInfo(Model model, @PathVariable int matterType, @PathVariable String companyName){
        model.addAttribute("matterType", matterType);
        model.addAttribute("companyName", companyName);

        return VIEW_PREFIX + "linkmatterInfo";
    }

    @RequestMapping(value = "/linkmatter/{matterType}/{companyName:.+}", method = RequestMethod.GET)
    public String linkmatter(Model model, @PathVariable int matterType, @PathVariable String companyName){

        List<MatterTemplate> matterTemplateList = matterTemplateService.findByType(matterType);
        model.addAttribute("matterCount", matterTemplateList.size());

        ReqData reqData = new ReqData();
        reqData.put("matterType", matterType);
        reqData.put("companyName", companyName);
        List<CompanyMatter> companyMatterList = companyMatterService.findByCompany(reqData);

        List<MatterTemplate> linkMatterTemplateList = new ArrayList<>();
        companyMatterList.stream().forEach(companyMatter -> {
            List<MatterTemplate> linkTemp = matterTemplateList.stream().filter(matterTemplate -> matterTemplate.getId()==companyMatter.getTemplateId()).collect(Collectors.toList());
            if(null!=linkTemp && linkTemp.size()>0){
                linkMatterTemplateList.add(linkTemp.get(0));
                matterTemplateList.remove(linkTemp.get(0));
            }
        });
        model.addAttribute("companyName", companyName);
        model.addAttribute("linkMatterTemplateList", linkMatterTemplateList);
        model.addAttribute("matterTemplateList", matterTemplateList);

        //自定义模板
        CustomMatter customMatter = new CustomMatter();
        customMatter.setType(matterType);
        customMatter.setKey(companyName);
        customMatter.setStatus(0);
        List<CustomMatter> customMatterList = customMatterService.findByKey(customMatter);
        long customLinkCount = customMatterList.stream().filter(customBondMatter -> customBondMatter.getRelation()==1).count();
        int customCount = null== customMatterList ? 0 : customMatterList.size();

        model.addAttribute("customMatterList", customMatterList);
        model.addAttribute("customCount", customCount);
        model.addAttribute("customLinkCount", customLinkCount);

        return VIEW_PREFIX + "linkmatter";
    }

    //保存
    @Operate(mark="修改关联事项",type = 301, operateType = 2)
    @ResponseBody
    @RequestMapping("/linkMatterSave")
    public RestResponse<String> linkMatterSave(@RequestBody(required = false) ReqData reqData) {
        RestResponse<String> result;

        try {
            List<CompanyMatter> companyMatterList = new ArrayList<>();
            String companyName = reqData.getString("companyName");
            if(null!=reqData.get("companyMatterList")){
                companyMatterList = (List<CompanyMatter>)reqData.get("companyMatterList");
            }
            ArrayList<Integer> customCompanyMatterIds = (ArrayList<Integer>)reqData.get("customCompanyMatterIds");
            Integer[] ids = customCompanyMatterIds.toArray(new Integer[customCompanyMatterIds.size()]);
            int count = companyMatterService.saveCompanyMatter(companyName, companyMatterList, ids);
            result = RestResponse.Success(companyName);

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("保存失败","");
        }

        return result;
    }

    @RequestMapping(value = "/info/{companyId}/{companyName:.+}", method = RequestMethod.GET)
    public String info(Model model, @PathVariable int companyId, @PathVariable String companyName) {
        model.addAttribute("companyId", companyId);
        model.addAttribute("companyName", companyName);
        return VIEW_PREFIX + "info";
    }

    @RequestMapping(value = "/infoCompany/{companyId}", method = RequestMethod.GET)
    public String infoCompany(Model model, @PathVariable int companyId) {
        Company comapny = companyService.findById(companyId);
        model.addAttribute("companyInfo", comapny);
        return VIEW_PREFIX + "infoCompany";
    }

    @ResponseBody
    @RequestMapping(value = "/findCompanyInfoByCompanyId/{companyId}", method = RequestMethod.GET)
    public Company findCompanyInfoByCompanyId(@PathVariable int companyId){
        Company comapny = companyService.findById(companyId);
        return comapny;
    }

    @RequestMapping(value = "/infoMattertemplate/{companyId}/{matterType}/{companyName:.+}", method = RequestMethod.GET)
    public String infoMattertemplate(Model model, @PathVariable int companyId, @PathVariable int matterType, @PathVariable String companyName) {
        List<MatterTemplate> matterTemplateList = matterTemplateService.findByCompanyId(companyId);
        model.addAttribute("matterTemplateList", matterTemplateList);

        CustomMatter customMatter = new CustomMatter();
        customMatter.setType(matterType);
        customMatter.setKey(companyName);
        customMatter.setRelation(1);
        customMatter.setStatus(0);
        List<CustomMatter> customMatterList = customMatterService.findCustomMatterByKey(customMatter);
        model.addAttribute("customMatterList", customMatterList);

        return VIEW_PREFIX + "infoMattertemplate";
    }

    @RequestMapping(value = "/infoAttachment/{companyId}", method = RequestMethod.GET)
    public String infoAttachment(Model model, @PathVariable int companyId) {
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        User user = userService.findById(id);
        String roleCode = "", roleName = "";
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            roleCode = "1";
            roleName = "项目人员";
        }else if(subject.hasRole(ConstantUtil.INSPECTOR_ROLE)){
            roleCode = "0";
            roleName = "督导人员";
        }
        model.addAttribute("roleCode", roleCode);
        model.addAttribute("userId", id);

        return VIEW_PREFIX + "infoAttachment";
    }

    //事项材料
    @ResponseBody
    @RequestMapping("/findDocumentFileInfo")
    public Map<String, Object> findDocumentFileInfo(@RequestBody(required = false) ReqData reqData) {
        PageInfo<DocumentFile> page = documentFileService.findCompanyAllByPage(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());
        return map;
    }

    //保存
    @Operate(mark="修改关联关系",type=101, operateType = 2)
    @ResponseBody
    @RequestMapping("/companyLinkSave")
    public RestResponse<Integer> companyLinkSave(@RequestBody List<CompanyMatter> companyMatterList) {
        RestResponse<Integer> result;

        try {
            int count = companyMatterService.saveLinkCompanyMatterList(companyMatterList);
            if(count > 0){
                result = RestResponse.Success(companyMatterList.get(0).getTemplateId());
            }else{
                result = RestResponse.Fail("保存失败",-1);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("保存失败",-1);
        }

        return result;
    }

    //保存
    @Operate(mark="修改关联关系",type=101, operateType = 3)
    @ResponseBody
    @RequestMapping("/deleteByTemplateId")
    public RestResponse<Integer> deleteByTemplateId(@RequestBody(required = false) ReqData reqData) {
        RestResponse<Integer> result;

        try {
            companyMatterService.deleteByTemplateId(reqData.getInteger("templateId").intValue());
            result = RestResponse.Success(reqData.getInteger("templateId").intValue());
        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("保存失败",-1);
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteLinkMan", method = RequestMethod.POST)
    public RestResponse<String> deleteLinkMan(@RequestBody CompanyMatter companyMatter){
        RestResponse<String> result;
        try{
            int resultStatus = companyMatterService.deleteLinkMan(companyMatter);
            if(resultStatus>0){
                result = RestResponse.Success("删除成功", "");
            }else{
                result = RestResponse.Fail("删除失败", "");
            }
        }catch(Exception ex){
            logger.info("删除失败:" + ex.getMessage());
            result = RestResponse.Fail("删除失败", "");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteByMatterList", method = RequestMethod.POST)
    public RestResponse<String> deleteByMatterList(@RequestBody List<CompanyMatter> companyMatterList){
        RestResponse<String> result;
        try{
            String [] companyName = new String [companyMatterList.size()];
            for(int i=0;i<companyMatterList.size();i++){
                companyName[i] = companyMatterList.get(i).getCompanyName();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", companyMatterList.get(0).getTemplateId());
            map.put("companynames", companyName);


            int resultStatus = companyMatterService.deleteByMatterList(map);
            if(resultStatus>0){
                result = RestResponse.Success( "删除成功", "");
            }else{
                result = RestResponse.Fail("删除失败","");
            }
        }catch(Exception ex){
            logger.info("删除失败:" + ex.getMessage());
            result = RestResponse.Fail("删除失败","");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/insertCompanyMatterList", method = RequestMethod.POST)
    public RestResponse<String> insertCompanyMatterList(@RequestBody List<CompanyMatter> companyMatterList){
        RestResponse<String> result;
        try{
            int resultStatus = companyMatterService.insertCompanyMatterList(companyMatterList);
            if(resultStatus>0){
                result = RestResponse.Success( "保存成功", "");
            }else{
                result = RestResponse.Fail("保存失败","");
            }
        }catch(Exception ex){
            logger.info("保存失败:" + ex.getMessage());
            result = RestResponse.Fail("保存失败","");
        }
        return result;
    }

    @RequestMapping(value = "/info/{companyName}", method = RequestMethod.GET)
    public String info(Model model,  @PathVariable String companyName) {
        model.addAttribute("companyId", companyService.findByName(companyName).getId());
        model.addAttribute("companyName", companyName);
        return VIEW_PREFIX + "info";
    }

    //历史记录
    @RequestMapping(value = "/linkmatterHistory/{companyName:.+}", method = RequestMethod.GET)
    public String linkmatterHistory(Model model, @PathVariable String companyName) throws Exception {
        ReqData reqData = new ReqData();
        reqData.put("type", 301);
        reqData.put("companyName", companyName);

        //关联历史记录
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){
            MatterTemplateMap.refreshMap();

            History oldHistory = historyList.get(0);
            int index = 0;
            for (History history: historyList) {
                HistoryLog historyLog = new HistoryLog();
                if(index==0){
                    historyLog.setLogDate(history.getCreateTime());
                    historyLog.setLogUserId(history.getUserId());
                    historyLog.setLogUser(history.getUserName());
                    historyLog.setLogMark(history.getMark());
                    historyLog.setLogOperateType(history.getOperateType());

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    JsonArray jsonArray = parser.parse(history.getArgs()).getAsJsonArray();
                    List<String> logContent = new ArrayList<>();
                    //正常模板事项
                    if(null!=jsonArray && jsonArray.size()>0 && null!=jsonArray.get(0).getAsJsonObject().get("companyMatterList")){
                        List<CompanyMatter> companyMatterList = gson.fromJson(jsonArray.get(0).getAsJsonObject().get("companyMatterList"), new TypeToken<List<CompanyMatter>>() {}.getType());
                        List<MatterTemplate> matterTemplateList = matterTemplateService.findMatterAll();

                        for(CompanyMatter companyMatter : companyMatterList){
                            MatterTemplate matterTemplate = matterTemplateList.stream().filter(m -> m.getId()==companyMatter.getTemplateId()).findFirst().get();
                            if(null!=matterTemplate && null!=matterTemplate.getName()){
                                logContent.add("<span style='font-weight: bold;'>" + matterTemplate.getName() + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                            }
                        }
                    }
                    //自定义模板
                    if(null!=jsonArray && jsonArray.size()>0 && null!=jsonArray.get(0).getAsJsonObject().get("customCompanyMatterIds")){
                        int[] customMatterIds = gson.fromJson(jsonArray.get(0).getAsJsonObject().get("customCompanyMatterIds"), new TypeToken<int[]>() {}.getType());
                        List<CustomMatter> customMatterList = customMatterService.findAllCustomMatter();
                        for(int i=0;i<customMatterIds.length;i++){
                            int id = customMatterIds[i];
                            CustomMatter customMatter = customMatterList.stream().filter(m -> m.getId()==id).findFirst().get();
                            if(null!=customMatter && null!=customMatter.getName()){
                                logContent.add("<span style='font-weight: bold;'>" + customMatter.getName() + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                            }
                        }
                    }
                    historyLog.setLogContent(logContent);
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

                    //正常模板事项
                    JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                    if(null!=oldAarray && oldAarray.size()>0 && null!=oldAarray.get(0).getAsJsonObject().get("companyMatterList")){
                        oldCompanyMatterList = gson.fromJson(oldAarray.get(0).getAsJsonObject().get("companyMatterList"), new TypeToken<List<CompanyMatter>>() {}.getType());
                    }

                    JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                    if(null!=nowAarray && nowAarray.size()>0 && null!=nowAarray.get(0).getAsJsonObject().get("companyMatterList")){
                        nowCompanyMatterList = gson.fromJson(nowAarray.get(0).getAsJsonObject().get("companyMatterList"), new TypeToken<List<CompanyMatter>>() {}.getType());
                    }

                    List<String> contentList = CompareObject.compareCompanyMatterList(oldCompanyMatterList, nowCompanyMatterList);

                    //自定义模板事项
                    List<Integer> oldIds = new ArrayList<>();
                    List<Integer> nowIds = new ArrayList<>();
                    if(null!=oldAarray && oldAarray.size()>0 && null!=oldAarray.get(0).getAsJsonObject().get("customCompanyMatterIds")){
                        Integer[] oldCustIds = gson.fromJson(oldAarray.get(0).getAsJsonObject().get("customCompanyMatterIds"), new TypeToken<Integer[]>() {}.getType());
                        oldIds = Arrays.asList(oldCustIds);
                    }
                    if(null!=nowAarray && nowAarray.size()>0 && null!=nowAarray.get(0).getAsJsonObject().get("customCompanyMatterIds")){
                        Integer[] nowCustIds = gson.fromJson(nowAarray.get(0).getAsJsonObject().get("customCompanyMatterIds"), new TypeToken<Integer[]>() {}.getType());
                        nowIds = Arrays.asList(nowCustIds);
                    }
                    if(null!=oldIds && null!=nowIds){
                        if(oldIds.size()>0 || nowIds.size()>0){
                            contentList.addAll(CompareObject.compareCustomMatter(oldIds, nowIds));
                        }
                    }

                    historyLog.setLogContent(contentList);
                }

                historyLogList.add(historyLog);
                oldHistory = history;
                index++;
            }
        }

        //自定义项目历史记录
        List<HistoryLog> customMatterHistoryLog = customMatterHistory(companyName);
        historyLogList.addAll(customMatterHistoryLog);

        model.addAttribute("historyLogList", historyLogList.stream()
                .filter(m->!m.getLogUserId().equals("000000"))
                .sorted(Comparator.comparing(HistoryLog::getLogDate).reversed()).collect(Collectors.toList()));
        return VIEW_PREFIX + "history";
    }

    public List<HistoryLog> customMatterHistory(String companyName) throws Exception {
        ReqData reqData = new ReqData();
        reqData.put("type", 4);
        reqData.put("companyName", companyName);

        //关联历史记录
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){
            CustomMatterMap.refreshMap();

            History oldHistory = historyList.get(0);
            for (History history: historyList) {
                HistoryLog historyLog = new HistoryLog();
                historyLog.setLogDate(history.getCreateTime());
                historyLog.setLogUserId(history.getUserId());
                historyLog.setLogUser(history.getUserName());
                historyLog.setLogMark(history.getMark());
                historyLog.setLogOperateType(history.getOperateType());

                CustomMatter oldCustomMatter = null;
                CustomMatter newCustomMatter = null;

                Gson gson = new Gson();
                JsonParser parser = new JsonParser();

                JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                if(null!=oldAarray && oldAarray.size()>0){
                    oldCustomMatter = gson.fromJson(oldAarray.get(0), CustomMatter.class);
                }

                JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                if(null!=nowAarray && nowAarray.size()>0){
                    newCustomMatter = gson.fromJson(nowAarray.get(0), CustomMatter.class);
                }

                if(history.getOperateType()==1){
                    historyLog.setLogMark(history.getMark() + "—" + newCustomMatter.getName());
                }else if(history.getOperateType()==3){
                    //historyLog.setLogMark(history.getMark() + "—" + oldCustomMatter.getName());
                    historyLog.setLogMark(history.getMark() + "—" + CustomMatterMap.map.get(newCustomMatter.getId()));
                }else{
                    historyLog.setLogMark(history.getMark() + "—" + newCustomMatter.getName());
                    List<String> contentList = CompareObject.compare(oldCustomMatter, newCustomMatter);
                    historyLog.setLogContent(contentList);
                }

                historyLogList.add(historyLog);
                oldHistory = history;
            }
        }

        return historyLogList.stream().filter(m->!m.getLogUserId().equals("000000")).collect(Collectors.toList());
    }
}

