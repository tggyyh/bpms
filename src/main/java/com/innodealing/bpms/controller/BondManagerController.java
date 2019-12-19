package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.model.BondManager;
import com.innodealing.bpms.model.BondMatter;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.BondManagerService;
import com.innodealing.bpms.service.BondService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.commons.http.RestResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bondmanager")
public class BondManagerController {

    @Autowired
    BondService bondService;
    @Autowired
    BondManagerService bondManagerService;
    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MatterTemplateController.class);
    private final static String VIEW_PREFIX = "app/bpms/bondmanager/";

    @RequestMapping(value = "/bondList/{bondCode:.+}", method = RequestMethod.GET)
    public String bondList(Model model, @PathVariable String bondCode) {
        Subject subject = SecurityUtils.getSubject();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            model.addAttribute("ismanager", 1);
        }else{
            model.addAttribute("ismanager", 0);
        }
        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        Bond bond = bondService.findByBondCode(bondCode);
        model.addAttribute("bond", bond);
        return VIEW_PREFIX + "list";
    }

    @RequestMapping(value = "/bondView/{bondCode:.+}", method = RequestMethod.GET)
    public String bondView(Model model, @PathVariable String bondCode) {
        Bond bond = bondService.findByBondCode(bondCode);
        List<User> userList = new ArrayList<>();
        if(null!=bond){
            for(BondManager bondManager : bond.getBondManagerList()){
                userList.add(bondManager.getSysUser());
            }
        }
        model.addAttribute(userList);
        return VIEW_PREFIX + "view";
    }



    @RequestMapping(value = "/other/{bondCode:.+}/{companyName:.+}", method = RequestMethod.GET)
    public String other(Model model, @PathVariable String bondCode, @PathVariable String companyName) {
        ReqData reqData = new ReqData();
        reqData.put("bondCode", bondCode);
        reqData.put("companyName", companyName);
        List<Bond> bondList = bondService.findOtherBond(reqData);
        model.addAttribute("bondList", bondList);
        return VIEW_PREFIX + "other";
    }

    //保存
    @ResponseBody
    @RequestMapping("/bondManagerSave")
    public RestResponse<String> bondManagerSave(@RequestBody(required = false) ReqData reqData) {
        RestResponse<String> result;

        try {
            List<BondManager> bondManagerList = new ArrayList<>();
            String bondCode = reqData.getString("bondCode");
            if(null!=reqData.get("bondManagerList")){
                bondManagerList = (List<BondManager>)reqData.get("bondManagerList");
            }
            int count = bondManagerService.bondManagerSave(bondCode, bondManagerList);
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

    @RequestMapping(value = "/bondman", method = RequestMethod.GET)
    public String bondman(Model model) {
        List<User> userList = userService.findByRoleCode("manager_handle");
        model.addAttribute("userList", userList);
        return VIEW_PREFIX + "bondman";
    }
}
