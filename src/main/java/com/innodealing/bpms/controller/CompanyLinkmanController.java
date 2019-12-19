package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.model.CompanyLinkman;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.BondService;
import com.innodealing.bpms.service.CompanyLinkmanService;
import com.innodealing.bpms.service.CompanyService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.commons.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/companylinkman")
public class CompanyLinkmanController {

    @Autowired
    CompanyLinkmanService companyLinkmanService;
    @Autowired
    BondService bondService;
    @Autowired
    UserService userService;
    @Autowired
    private CompanyService companyService;

    private static final Logger logger = LoggerFactory.getLogger(MatterTemplateController.class);
    private final static String VIEW_PREFIX = "app/bpms/companylinkman/";

    @RequestMapping(value = "/list/{companyId}", method = RequestMethod.GET)
    public String index(Model model, @PathVariable int companyId) {
        List<CompanyLinkman> companyLinkmanList = companyLinkmanService.findByCompanyId(companyId);
        model.addAttribute("companyLinkmanList", companyLinkmanList);
        return VIEW_PREFIX + "list";
    }

    @RequestMapping(value = "/bondlinkman/{companyName:.+}", method = RequestMethod.GET)
    public String bondlinkman(Model model, @PathVariable String companyName) {
        List<CompanyLinkman> companyLinkmanList = companyLinkmanService.findByCompanyName(companyName);
        model.addAttribute("companyLinkmanList", companyLinkmanList);
        return VIEW_PREFIX + "bondlinkman";
    }

    @RequestMapping(value = "/bondmanagerlist/{companyId}", method = RequestMethod.GET)
    public String companyList(Model model, @PathVariable int companyId) {
        List<User> userList = userService.findByRoleCode("manager_handle");
        //userList = null==userList ? new ArrayList<User>() : userList;
        model.addAttribute("userList", userList);
        List<Bond> bondList = bondService.findByCompanyId(companyId);
        //bond = null==bond ? new Bond() : bond;
        model.addAttribute("bondList", bondList);
        return VIEW_PREFIX + "bondmanagerlist";
    }

    //添加
    @ResponseBody
    @RequestMapping("/companyLinkmanInsert")
    public RestResponse<String> companyLinkmanInsert(@RequestBody CompanyLinkman companyLinkman) {
        RestResponse<String> result;

        try {
            int count = companyLinkmanService.insertCompanyLinkman(companyLinkman);
            if(count > 0){
                result = RestResponse.Success("", String.valueOf(companyLinkman.getId()));
            }else{
                result = RestResponse.Fail("添加失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("添加失败","");
        }

        return result;
    }

    //更新
    @ResponseBody
    @RequestMapping("/companyLinkmanUpdate")
    public RestResponse<String> companyLinkmanUpdate(@RequestBody CompanyLinkman companyLinkman) {
        RestResponse<String> result;

        try {
            int count = companyLinkmanService.updateCompanyLinkman(companyLinkman);
            if(count > 0){
                result = RestResponse.Success("");
            }else{
                result = RestResponse.Fail("更新失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("更新失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/companyLinkmanDelete/{id}", method = RequestMethod.GET)
    public RestResponse<String> companyLinkmanDelete(@PathVariable int id){
        RestResponse<String> result;
        try{
            int resultStatus = companyLinkmanService.deleteCompanyLinkman(id);
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
    @RequestMapping(value = "/listLinkman/{companyName}", method = RequestMethod.GET)
    public String listLinkman(Model model, @PathVariable String companyName) {
        int companyId = companyService.findByName(companyName).getId();
        List<CompanyLinkman> companyLinkmanList = companyLinkmanService.findByCompanyId(companyId);
        model.addAttribute("companyLinkmanList", companyLinkmanList);
        return VIEW_PREFIX + "view";
    }

    @RequestMapping(value = "/bondman", method = RequestMethod.GET)
    public String bondman(Model model) {
        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        return VIEW_PREFIX + "bondman";
    }
}
