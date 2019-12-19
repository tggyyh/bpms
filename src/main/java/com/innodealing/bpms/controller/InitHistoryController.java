package com.innodealing.bpms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.innodealing.bpms.domain.WSSynService;
import com.innodealing.bpms.model.*;
import com.innodealing.bpms.service.*;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/history")
public class InitHistoryController {
   @Autowired
   private MatterTemplateService matterTemplateService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BondService bondService;
    @Autowired
    private InitHistoryService initHistoryService;
    @Autowired
    private CustomMatterService customMatterService;
    @Autowired
    private BondMatterService bondMatterService;

    private static final Logger log = LoggerFactory.getLogger(InitHistoryController.class);
    private static final String MANUAL_TASK="manual_task";
    @ResponseBody
    @RequestMapping(value="/init",method = RequestMethod.GET)
    public RestResponse<String> startProcess() {
        try {
            Gson gson = new Gson();
            List<MatterTemplate> list1 = matterTemplateService.findAll();
            for(MatterTemplate mt:list1){
                Object[] args = new Object[1];
                args[0]=mt;
                if(null == mt.getTemplateAttachmentList()){
                    mt.setTemplateAttachmentList(new ArrayList());
                }
                String mtString = gson.toJson(args);
                History h = new History();
                h.setArgs(mtString);
                h.setMark("初始化");
                h.setType(1);
                h.setUserId("000000");
                h.setTemplateId(mt.getId());
                historyService.insertHistory(h);
            }
            for(MatterTemplate mt:list1){
                List<CompanyMatter> list = null;
                if(0==mt.getType()) {
                     list = initHistoryService.findCompanyMatter(mt.getId());
                }else {
                    list = initHistoryService.findBondMatter(mt.getId());
                }
                if(null!=list && list.size()>0){
                    Object[] args = new Object[1];
                    args[0]=list;
                    String mtString = gson.toJson(args);
                    History h = new History();
                    h.setArgs(mtString);
                    h.setMark("初始化");
                    h.setType(101);
                    h.setUserId("000000");
                    h.setTemplateId(mt.getId());
                    historyService.insertHistory(h);
                }
            }
         List<Bond> bonds = initHistoryService.findAllBonds();
         for(Bond bond:bonds){
                 if(null!=bond){
                     if(!CollectionUtils.isEmpty(bond.getBankManagerList())){
                        List<BankManager>  bankManagers = bond.getBankManagerList()
                                .stream().sorted((Comparator.comparing(BankManager::getId)))
                                        .collect(Collectors.toList());
                        bond.setBankManagerList(bankManagers);

                     }
                     if(!CollectionUtils.isEmpty(bond.getBondManagerList())){
                         List<BondManager>  BondManagers = bond.getBondManagerList()
                                 .stream().sorted((Comparator.comparing(BondManager::getId)))
                                 .collect(Collectors.toList());
                         bond.setBondManagerList(BondManagers);

                     }
                     if(null != bond.getCompany()) {
                         if (!CollectionUtils.isEmpty(bond.getCompany().getCompanyLinkmanList())) {
                             List<CompanyLinkman> companyLinkmans = bond.getCompany().getCompanyLinkmanList()
                                     .stream().sorted((Comparator.comparing(CompanyLinkman::getId)))
                                     .collect(Collectors.toList());
                             bond.getCompany().setCompanyLinkmanList(companyLinkmans);

                         }
                     }
                    Object[] args = new Object[1];
                    args[0]=bond;
                    String mtString = gson.toJson(args);
                    History h = new History();
                    h.setArgs(mtString);
                    h.setMark("初始化");
                    h.setType(2);
                    h.setOperateType(2);
                    h.setUserId("000000");
                    h.setBondCode(bond.getCode());
                    historyService.insertHistory(h);
                }
         }
             List<CustomMatter> customMatters = customMatterService.findAllCustomMatter();
             for(CustomMatter cmr:customMatters){
                    CustomMatter cm = customMatterService.findById(cmr.getId());
                 Object[] args = new Object[1];
                args[0]=cm;
                if(null ==cm){
                    continue;
                }
                if(null == cm.getCustomAttachmentList()){
                    cm.setCustomAttachmentList(new ArrayList());
                }
                String mtString = gson.toJson(args);
                History h = new History();
                h.setArgs(mtString);
                h.setMark("初始化");
                h.setType(4);
                h.setOperateType(2);
                h.setUserId("000000");
                h.setCustomId(cm.getId());
                if(cm.getType()==0){
                    h.setCompanyName(cm.getKey());
                }else{
                    h.setBondCode(cm.getKey());
                }
                historyService.insertHistory(h);

             }
               List<String> codes = initHistoryService.findAllBondCodes();
               for(String code:codes) {
                   Object[] args = new Object[1];
                   List<BondMatter> bondMatterList = initHistoryService.findByBondCode(code);
                   List<Integer> customMatterList = initHistoryService.findByKey(code);
                   Map map1 = new HashMap();
                   if (null == bondMatterList) {
                       bondMatterList = new ArrayList();
                   }
                   if (null == customMatterList) {
                       customMatterList = new ArrayList();
                   }
                   map1.put("bondMatterList", bondMatterList);
                   map1.put("customBondMatterIds", customMatterList);
                   map1.put("bondCode", code);
                   args[0] = map1;
                   String mtString = gson.toJson(args);
                   History h = new History();
                   h.setArgs(mtString);
                   h.setMark("初始化");
                   h.setType(201);
                   h.setOperateType(2);
                   h.setUserId("000000");
                   h.setBondCode(code);
                   historyService.insertHistory(h);
               }
               List<String> names = initHistoryService.findAllCompanys();
               for(String name:names){
                   Object[] args = new Object[1];
                   List<CompanyMatter> companyMatterList = initHistoryService.findByName(name);
                   List<Integer> customCompanyMatterIds = initHistoryService.findByKey(name);
                   Map map= new HashMap();
                   if(null == companyMatterList){
                       companyMatterList = new ArrayList();
                   }
                   if(null == customCompanyMatterIds){
                       customCompanyMatterIds = new ArrayList();
                   }

                   map.put("customCompanyMatterIds",customCompanyMatterIds);
                   map.put("companyName",name);
                   map.put("companyMatterList",companyMatterList);
                   args[0]=map;
                   String mtString = gson.toJson(args);
                    History h = new History();
                    h.setArgs(mtString);
                    h.setMark("初始化");
                    h.setType(301);
                    h.setOperateType(2);
                    h.setUserId("000000");
                    h.setCompanyName(name);
                    historyService.insertHistory(h);
               }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return RestResponse.Fail("");
        }
        return RestResponse.Success("");
    }

}
