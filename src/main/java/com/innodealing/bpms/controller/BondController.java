package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.innodealing.bpms.appconfig.history.CompareObject;
import com.innodealing.bpms.appconfig.history.Operate;
import com.innodealing.bpms.appconfig.history.commap.*;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.HistoryLog;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.*;
import com.innodealing.commons.http.RestResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bond")
public class BondController {

    @Autowired
    BondService bondService;
    @Autowired
    BondMatterService bondMatterService;
    @Autowired
    AreaService areaService;
    @Autowired
    MatterTemplateService matterTemplateService;
    @Autowired
    CustomMatterService customMatterService;
    @Autowired
    UserService userService;
    @Autowired
    CompanyLinkmanService companyLinkmanService;
    @Autowired
    BankManagerService bankManagerService;
    @Autowired
    ProcessInfoService processInfoService;
    @Autowired
    IndustryService industryService;
    @Autowired
    EconomicDepartmentService economicDepartmentService;
    @Autowired
    DocumentFileService documentFileService;
    @Autowired
    CompanyService companyService;
    @Autowired
    HistoryService historyService;

    private static final Logger logger = LoggerFactory.getLogger(MatterTemplateController.class);
    private final static String VIEW_PREFIX = "app/bpms/bond/";

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        Subject subject = SecurityUtils.getSubject();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            model.addAttribute("ismanager", 1);
        }else{
            model.addAttribute("ismanager", 0);
        }

        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        return VIEW_PREFIX + "index";
    }

    @ResponseBody
    @RequestMapping(value = "/findArea", method = RequestMethod.GET)
    public List<Area> findArea(){
        List<Area> areaList = areaService.findAll();
        return areaList;
    }

    @ResponseBody
    @RequestMapping(value = "/findIndustry", method = RequestMethod.GET)
    public List<Industry> findIndustry(){
        List<Industry> industryList = industryService.findAll();
        return industryList;
    }

    @ResponseBody
    @RequestMapping(value = "/findEconomicDepartment", method = RequestMethod.GET)
    public List<EconomicDepartment> findEconomicDepartment(){
        List<EconomicDepartment> economicDepartmentList = economicDepartmentService.findAll();
        return economicDepartmentList;
    }

    @ResponseBody
    @RequestMapping(value = "/findAll", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findAll(@RequestBody(required = false) ReqData reqData){
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            reqData.put("manageruser", id);
        }

        PageInfo<Bond> page = bondService.findAll(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());
        return map;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {
        return VIEW_PREFIX + "add";
    }

    @RequestMapping(value = "/editInfo/{id}/{bondCode:.+}", method = RequestMethod.GET)
    public String editInfo(Model model, @PathVariable int id, @PathVariable String bondCode) {
        model.addAttribute("bondId", id);
        model.addAttribute("bondCode", bondCode);
        return VIEW_PREFIX + "editInfo";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String add(Model model, @PathVariable int id) {
        Bond bond = bondService.findByBondId(id);
        model.addAttribute("bond", bond);
        return VIEW_PREFIX + "edit";
    }

    @RequestMapping(value = "/bondman", method = RequestMethod.GET)
    public String bondman(Model model) {
        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        return VIEW_PREFIX + "bondman";
    }

    @RequestMapping(value = "/linkmatterInfo/{matterType}/{bondCode:.+}", method = RequestMethod.GET)
    public String linkmatterInfo(Model model, @PathVariable int matterType, @PathVariable String bondCode){
        model.addAttribute("matterType", matterType);
        model.addAttribute("bondCode", bondCode);

        return VIEW_PREFIX + "linkmatterInfo";
    }

    @RequestMapping(value = "/linkmatter/{matterType}/{bondCode:.+}", method = RequestMethod.GET)
    public String linkmatter(Model model, @PathVariable int matterType, @PathVariable String bondCode){

        List<MatterTemplate> matterTemplateList = matterTemplateService.findByType(matterType);
        model.addAttribute("matterCount", matterTemplateList.size());

        List<BondMatter> bondMatterList = bondMatterService.findByBondCode(bondCode);

        List<MatterTemplate> linkMatterTemplateList = new ArrayList<>();
        bondMatterList.stream().forEach(companyMatter -> {
            List<MatterTemplate> linkTemp = matterTemplateList.stream().filter(matterTemplate -> matterTemplate.getId()==companyMatter.getTemplateId()).collect(Collectors.toList());
            if(null!=linkTemp && linkTemp.size()>0){
                linkMatterTemplateList.add(linkTemp.get(0));
                matterTemplateList.remove(linkTemp.get(0));
            }
        });
        model.addAttribute("bondCode", bondCode);
        model.addAttribute("linkMatterTemplateList", linkMatterTemplateList);
        model.addAttribute("matterTemplateList", matterTemplateList);

        //自定义模板
        CustomMatter customMatter = new CustomMatter();
        customMatter.setType(matterType);
        customMatter.setKey(bondCode);
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
    @Operate(mark="修改关联事项",type = 201, operateType = 2)
    @ResponseBody
    @RequestMapping("/linkMatterSave")
    public RestResponse<String> linkMatterSave(@RequestBody(required = false) ReqData reqData) {
        RestResponse<String> result;

        try {
            List<BondMatter> bondMatterList = new ArrayList<>();
            String bondCode = reqData.getString("bondCode");
            if(null!=reqData.get("bondMatterList")){
                bondMatterList = (List<BondMatter>)reqData.get("bondMatterList");
            }
            ArrayList<Integer> customBondMatterIds = (ArrayList<Integer>)reqData.get("customBondMatterIds");
            Integer[] ids = customBondMatterIds.toArray(new Integer[customBondMatterIds.size()]);
            int count = bondMatterService.saveBondMatter(bondCode, bondMatterList, ids);
            result = RestResponse.Success(bondCode);

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("保存失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteSave", method = RequestMethod.POST)
    public RestResponse<String> deleteSave(@RequestBody int[] ids){
        RestResponse<String> result;
        try{
            if(null!=ids && ids.length>0){
                int resultStatus = bondService.deleteByIds(ids);
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

    @RequestMapping(value = "/info/{bondId}/{bondCode:.+}/{companyId}", method = RequestMethod.GET)
    public String info(Model model, @PathVariable int bondId, @PathVariable String bondCode, @PathVariable int companyId) {
        model.addAttribute("bondId", bondId);
        model.addAttribute("bondCode", bondCode);
        model.addAttribute("companyId", companyId);
        return VIEW_PREFIX + "info";
    }

    @RequestMapping(value = "/infoBond/{bondId}", method = RequestMethod.GET)
    public String infoBond(Model model, @PathVariable int bondId) {
        Bond bond = bondService.findByBondId(bondId);
        model.addAttribute("bondInfo", bond);
        return VIEW_PREFIX + "infoBond";
    }

    @RequestMapping(value = "/infoMattertemplate/{bondType}/{bondCode:.+}", method = RequestMethod.GET)
    public String infoMattertemplate(Model model, @PathVariable int bondType, @PathVariable String bondCode) {
        ReqData reqData = new ReqData();
        reqData.put("matterType", bondType);
        reqData.put("bondCode", bondCode);
        List<BondMatter> bondMatterList = bondMatterService.findBondMatterByBondCode(reqData);
        model.addAttribute("bondMatterList", bondMatterList);

        CustomMatter customMatter = new CustomMatter();
        customMatter.setType(bondType);
        customMatter.setKey(bondCode);
        customMatter.setRelation(1);
        customMatter.setStatus(0);
        List<CustomMatter> customMatterList = customMatterService.findCustomMatterByKey(customMatter);
        model.addAttribute("customMatterList", customMatterList);
        return VIEW_PREFIX + "infoMattertemplate";
    }

    @ResponseBody
    @RequestMapping(value = "/findLinkman", method = RequestMethod.POST)
    public Bond findLinkman(@RequestBody(required = false) ReqData reqData){

        String bondCode = reqData.getString("bondCode");
        String companyName = reqData.getString("companyName");

        //读取项目信息，包括项目负责人
        Bond bond = bondService.findBondManagerByCode(bondCode);
        if(null==bond){
            bond = new Bond();
            bond.setCode(bondCode);
            bond.setCompanyName(companyName);
        }
        if(null==bond.getBondManagerList()){
            List<BondManager> bondManagerList = new ArrayList<>();
            bond.setBondManagerList(bondManagerList);
        }

        //读取发行人信息
        Company company = companyService.findByName(companyName);
        if(null==company){
            company = new Company();
            company.setName(companyName);
        }

        //发行人对接人
        List<CompanyLinkman> companyLinkmanList = companyLinkmanService.findByCompanyName(companyName);
        if(null!=companyLinkmanList && companyLinkmanList.size()>0){
            company.setCompanyLinkmanList(companyLinkmanList);
        }
        bond.setCompany(company);

        //监管银行负责人
        List<BankManager> bankManagerList = bankManagerService.findByBondCode(bondCode);
        if(null!=bankManagerList && bankManagerList.size()>0){
            bond.setBankManagerList(bankManagerList);
        }


        //设置其它项目负责人
        List<Bond> otherBondList = bondService.findOtherBond(reqData);
        if(null!=otherBondList && otherBondList.size()>0){
            for(Bond otherBond : otherBondList){
                if(null!=otherBond.getBondManagerList() && otherBond.getBondManagerList().size()>0){
                    for(BondManager bondManager : otherBond.getBondManagerList()){
                        bond.getBondManagerList().add(bondManager);
                    }
                }
            }
        }

        return bond;
    }

    @RequestMapping(value = "/infoAttachment/{bondCode:.+}", method = RequestMethod.GET)
    public String infoAttachment(Model model, @PathVariable String bondCode) {
        Bond bond = bondService.findBondByCode(bondCode);
        model.addAttribute("attachmentBondCode", bond.getCode());
        model.addAttribute("attachmentBondName", bond.getShortname());
        model.addAttribute("attachmentCompanyName", bond.getCompanyName());

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
        PageInfo<DocumentFile> page = documentFileService.findBondAllByPage(reqData);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());
        return map;
    }

    //保存
    @Operate(mark="新增项目", type = 2, operateType = 1)
    @ResponseBody
    @RequestMapping("/addSave")
    public RestResponse<String> addSave(@RequestBody Bond bond) {
        RestResponse<String> result;

        try {
            int count = bondService.addSave(bond);
            if(count <= 0){
                result = RestResponse.Fail("保存失败", "");
            }else{
                result = RestResponse.Success(bond.getCode());
            }

        }catch (DuplicateKeyException d){
            d.printStackTrace();
            result = RestResponse.Fail("保存失败,该数据已存在", "");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result = RestResponse.Fail("提交失败", "");
        }

        return result;
    }

    //保存
    @Operate(mark="修改项目",type = 2, operateType = 2)
    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<String> editSave(@RequestBody Bond bond) {
        RestResponse<String> result;

        try {
            int count = bondService.editSave(bond);
            if(count <= 0){
                result = RestResponse.Fail("保存失败", "");
            }else{
                result = RestResponse.Success(bond.getCode());
            }

        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result = RestResponse.Fail("提交失败", "");
        }

        return result;
    }

    //根据发行人姓名查询发行人
    @ResponseBody
    @RequestMapping(value = "/SearchCompanyByName/{companyname}",method = RequestMethod.GET)
    public Company SearchCompanyByName(@PathVariable String companyname){
        //companyCount,name,stockCode,industryScale,
        // industryType,industryBigType,economicDepartment,economicDepartmentDetail,
        // economicSector,provinceCode,cityCode,ratingCompany1,
        //corporateRating1,ratingCompany2,corporateOrgCode2,corporateRating2
        return bondService.SearchCompanyByName(companyname);
    }


    //保存
    @Operate(mark="修改关联关系",type=101, operateType = 2)
    @ResponseBody
    @RequestMapping("/bondLinkSave")
    public RestResponse<Integer> bondLinkSave(@RequestBody List<BondMatter> bondMatterList) {
        RestResponse<Integer> result;

        try {
            int count = bondMatterService.saveLinkBondMatterList(bondMatterList);
            if(count > 0){
                result = RestResponse.Success(bondMatterList.get(0).getTemplateId());
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
    public RestResponse<String> deleteByTemplateId(@RequestBody(required = false) ReqData reqData) {
        RestResponse<String> result;

        try {
            bondMatterService.deleteByTemplateId(reqData.getInteger("templateId").intValue());
            result = RestResponse.Success("");
        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("保存失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteLinkMan", method = RequestMethod.POST)
    public RestResponse<String> deleteLinkMan(@RequestBody BondMatter bondMatter){
        RestResponse<String> result;
        try{
            int resultStatus = bondMatterService.deleteLinkMan(bondMatter);
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
    @RequestMapping(value = "/deleteByMatterList", method = RequestMethod.POST)
    public RestResponse<String> deleteByMatterList(@RequestBody List<BondMatter> bondMatterList){
        RestResponse<String> result;
        try{
            String [] bondCode = new String [bondMatterList.size()];
            for(int i=0;i<bondMatterList.size();i++){
                bondCode[i] = bondMatterList.get(i).getBondCode();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("templateId", bondMatterList.get(0).getTemplateId());
            map.put("bondcodes", bondCode);


            int resultStatus = bondMatterService.deleteByMatterList(map);
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
    @RequestMapping(value = "/insertBondMatterList", method = RequestMethod.POST)
    public RestResponse<String> insertBondMatterList(@RequestBody List<BondMatter> bondMatterList){
        RestResponse<String> result;
        try{
            int resultStatus = bondMatterService.insertBondMatterList(bondMatterList);
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

    //设置到期
    @Operate(mark="修改项目", type = 2, operateType = 4)
    @ResponseBody
    @RequestMapping("/setdate")
    public RestResponse<String> setdate(@RequestBody Bond bond) {
        RestResponse<String> result;

        try {
            bond.setStatus(2);
            int count = bondService.updateSetBondExpire(bond);
            if(count > 0){
                result = RestResponse.Success(bond.getCode());
            }else{
                result = RestResponse.Fail("设置到期失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("设置到期失败","");
        }

        return result;
    }

    //撤消到期
    @Operate(mark="修改项目", type = 2, operateType = 5)
    @ResponseBody
    @RequestMapping("/resetdate")
    public RestResponse<String> resetdate(@RequestBody Bond bond) {
        RestResponse<String> result;

        try {
            bond.setStatus(0);
            int count = bondService.updateResetBondExpire(bond);
            if(count > 0){
                result = RestResponse.Success(bond.getCode());
            }else{
                result = RestResponse.Fail("撤消到期失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("撤消到期失败","");
        }

        return result;
    }

    //删除
    @Operate(mark="删除项目",type = 2, operateType = 3)
    @ResponseBody
    @RequestMapping("/remove")
    public RestResponse<String> remove(@RequestBody Bond bond) {
        RestResponse<String> result;

        try {
            bond.setStatus(4);
            int count = bondService.removeBond(bond);
            if(count > 0){
                result = RestResponse.Success(bond.getCode());
            }else{
                result = RestResponse.Fail("删除失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("删除失败","");
        }

        return result;
    }

    @RequestMapping(value = "/info/{bondCode:.+}", method = RequestMethod.GET)
    public String info(Model model,  @PathVariable String bondCode) {
        Bond bond = bondService.findBondByCode(bondCode);
        model.addAttribute("bondId", bond.getId());
        model.addAttribute("bondCode", bondCode);
        model.addAttribute("companyId", companyService.findByName(bond.getCompanyName()).getId());
        return VIEW_PREFIX + "info";
    }

    //事项材料
    @ResponseBody
    @RequestMapping("/findIsExistence")
    public String findIsExistence(@RequestBody(required = false) ReqData reqData) {
        String code = reqData.getString("bondCode");
        int count = bondService.findIsExistence(code);
        return String.valueOf(count);
    }

    //导出
    @ResponseBody
    @RequestMapping(value = "/downExcel", method = RequestMethod.POST)
    public void downExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReqData reqData = new ReqData(request);
        try {
            Subject subject = SecurityUtils.getSubject();
            String id = (String) subject.getPrincipals().getPrimaryPrincipal();
            if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
                //项目人员
                reqData.put("manageruser", id);
            }
            String listedPlace = reqData.getString("listedPlace");
            if(listedPlace.equals("")){
                reqData.put("listedPlace", null);
            }else{
                String[] aryListedPlace = listedPlace.split(",");
                ArrayList listedPlaceList = new ArrayList(Arrays.asList(aryListedPlace));
                reqData.put("listedPlace", listedPlaceList);
            }
            String type = reqData.getString("type");
            if(type.equals("")){
                reqData.put("type", null);
            }else{
                String[] aryType = type.split(",");
                ArrayList typeList = new ArrayList(Arrays.asList(aryType));
                reqData.put("type", typeList);
            }
            String userId = reqData.getString("userId");
            if(userId.equals("")){
                reqData.put("userId", null);
            }else{
                String[] aryUserId = userId.split(",");
                ArrayList userIdList = new ArrayList(Arrays.asList(aryUserId));
                reqData.put("userId", userIdList);
            }
            Workbook wb = bondService.downExcel(reqData);
            String fileName = new String("项目数据".getBytes("gb2312"), "ISO8859-1");
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
            response.setHeader("Content-Type","application/vnd.ms-excel;charset=utf-8");
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            logger.error("导出报错"+ e.getMessage());
        }
    }

    //历史记录
    @RequestMapping(value = "/history/{bondCode:.+}", method = RequestMethod.GET)
    public String history(Model model, @PathVariable String bondCode) throws Exception {
        ReqData reqData = new ReqData();
        reqData.put("type", 2);
        reqData.put("bondCode", bondCode);
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){
            UserMap.refreshMap();
            AreaMap.refreshMap();
            IndustryMap.refreshMap();
            EconomicDepartmentMap.refreshMap();

            History oldHistory = historyList.get(0);
            for (History history: historyList) {
                HistoryLog historyLog = new HistoryLog();
                historyLog.setLogDate(history.getCreateTime());
                historyLog.setLogUserId(history.getUserId());
                historyLog.setLogUser(history.getUserName());
                historyLog.setLogMark(history.getMark());
                historyLog.setLogOperateType(history.getOperateType());

                Bond oldBond = null;
                Bond nowBond = null;

                Gson gson = new Gson();
                JsonParser parser = new JsonParser();

                JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                if(null!=oldAarray && oldAarray.size()>0){
                    oldBond = gson.fromJson(oldAarray.get(0), Bond.class);
                }

                JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                if(null!=nowAarray && nowAarray.size()>0){
                    nowBond = gson.fromJson(nowAarray.get(0), Bond.class);
                }
                List<String> contentList = new ArrayList<>();
                if(history.getOperateType()==4){
                    contentList.add("<span style='font-weight: bold;'>设置到期</span>：由“未到期” <span style='font-weight: bold;'>修改为</span> “已到期”");
                }else if(history.getOperateType()==5) {
                    contentList.add("<span style='font-weight: bold;'>撤消到期</span>：由“已到期” <span style='font-weight: bold;'>修改为</span> “未到期”");
                }else {
                    contentList = CompareObject.compare(oldBond, nowBond);
                }
                historyLog.setLogContent(contentList);
                historyLogList.add(historyLog);
                oldHistory = history;
            }
        }

        model.addAttribute("historyLogList",
                historyLogList.stream()
                .filter(m-> !m.getLogUserId().equals("000000"))
                .sorted(Comparator.comparing(HistoryLog::getLogDate).reversed())
                .collect(Collectors.toList()));
        return VIEW_PREFIX + "history";
    }


    //历史记录
    @RequestMapping(value = "/linkmatterHistory/{bondCode:.+}", method = RequestMethod.GET)
    public String linkmatterHistory(Model model, @PathVariable String bondCode) throws Exception {
        ReqData reqData = new ReqData();
        reqData.put("type", 201);
        reqData.put("bondCode", bondCode);

        //关联历史记录
        List<History> historyList = historyService.findAll(reqData);
        List<HistoryLog> historyLogList = new ArrayList<>();
        if(null!=historyList && historyList.size()>0){

            MatterTemplateMap.refreshMap();
            CustomMatterMap.refreshMap();

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
                    if(null!=jsonArray && jsonArray.size()>0 && null!=jsonArray.get(0).getAsJsonObject().get("bondMatterList")){
                        List<BondMatter> bondMatterList = gson.fromJson(jsonArray.get(0).getAsJsonObject().get("bondMatterList"), new TypeToken<List<BondMatter>>() {}.getType());
                        List<MatterTemplate> matterTemplateList = matterTemplateService.findMatterAll();

                        for(BondMatter bondMatter : bondMatterList){
                            MatterTemplate matterTemplate = matterTemplateList.stream().filter(m -> m.getId()==bondMatter.getTemplateId()).findFirst().get();
                            if(null!=matterTemplate && null!=matterTemplate.getName()){
                                logContent.add("<span style='font-weight: bold;'>" + matterTemplate.getName() + "</span>：由 “未勾选” <span style='font-weight: bold;'>修改为</span> “勾选”");
                            }
                        }
                    }
                    //自定义模板
                    if(null!=jsonArray && jsonArray.size()>0 && null!=jsonArray.get(0).getAsJsonObject().get("customBondMatterIds")){
                        int[] customMatterIds = gson.fromJson(jsonArray.get(0).getAsJsonObject().get("customBondMatterIds"), new TypeToken<int[]>() {}.getType());
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

                    List<Object> oldBondMatterList = new ArrayList<>();
                    List<Object> nowBondMatterList = new ArrayList<>();

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    //正常模板事项
                    JsonArray oldAarray = parser.parse(oldHistory.getArgs()).getAsJsonArray();
                    if(null!=oldAarray && oldAarray.size()>0 && null!=oldAarray.get(0).getAsJsonObject().get("bondMatterList")){
                        oldBondMatterList = gson.fromJson(oldAarray.get(0).getAsJsonObject().get("bondMatterList"), new TypeToken<List<BondMatter>>() {}.getType());
                    }

                    JsonArray nowAarray = parser.parse(history.getArgs()).getAsJsonArray();
                    if(null!=nowAarray && nowAarray.size()>0 && null!=nowAarray.get(0).getAsJsonObject().get("bondMatterList")){
                        nowBondMatterList = gson.fromJson(nowAarray.get(0).getAsJsonObject().get("bondMatterList"), new TypeToken<List<BondMatter>>() {}.getType());
                    }

                    List<String> contentList = CompareObject.compareBondMatterList(oldBondMatterList, nowBondMatterList);

                    //自定义模板事项
                    List<Integer> oldIds = new ArrayList<>();
                    List<Integer> nowIds = new ArrayList<>();
                    if(null!=oldAarray && oldAarray.size()>0 && null!=oldAarray.get(0).getAsJsonObject().get("customBondMatterIds")){
                        Integer[] oldCustIds = gson.fromJson(oldAarray.get(0).getAsJsonObject().get("customBondMatterIds"), new TypeToken<Integer[]>() {}.getType());
                        oldIds = Arrays.asList(oldCustIds);
                    }
                    if(null!=nowAarray && nowAarray.size()>0 && null!=nowAarray.get(0).getAsJsonObject().get("customBondMatterIds")){
                        Integer[] nowCustIds = gson.fromJson(nowAarray.get(0).getAsJsonObject().get("customBondMatterIds"), new TypeToken<Integer[]>() {}.getType());
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
        List<HistoryLog> customMatterHistoryLog = customMatterHistory(bondCode);
        historyLogList.addAll(customMatterHistoryLog);

        model.addAttribute("historyLogList", historyLogList.stream().filter(m-> !m.getLogUserId().equals("000000")).sorted(Comparator.comparing(HistoryLog::getLogDate).reversed()).collect(Collectors.toList()));
        return VIEW_PREFIX + "history";
    }

    public List<HistoryLog> customMatterHistory(String bondCode) throws Exception {
        ReqData reqData = new ReqData();
        reqData.put("type", 4);
        reqData.put("bondCode", bondCode);
        reqData.put("orderby", "custom_id");

        //关联历史记录
        List<History> historyListAll = historyService.findAll(reqData);
        List<History> historyList = historyListAll.stream().sorted(Comparator.comparing(History::getCustomId)).collect(Collectors.toList());

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
                    if(oldHistory.getCustomId()==history.getCustomId()){
                        historyLog.setLogMark(history.getMark() + "—" + newCustomMatter.getName());
                        List<String> contentList = CompareObject.compare(oldCustomMatter, newCustomMatter);
                        historyLog.setLogContent(contentList);
                    }

                }

                historyLogList.add(historyLog);
                oldHistory = history;
            }
        }

        return historyLogList.stream().filter(m->!m.getLogUserId().equals("000000")).collect(Collectors.toList());
    }
}
