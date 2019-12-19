package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.model.BankManager;
import com.innodealing.bpms.service.BankManagerService;
import com.innodealing.commons.http.RestResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bankmanager")
public class BankManagerController {

    @Autowired
    BankManagerService bankManagerService;

    private static final Logger logger = LoggerFactory.getLogger(BankManagerController.class);
    private final static String VIEW_PREFIX = "app/bpms/bankmanager/";

    @RequestMapping(value = "/list/{bondCode:.+}", method = RequestMethod.GET)
    public String list(Model model, @PathVariable String bondCode) {
        Subject subject = SecurityUtils.getSubject();
        if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
            //项目人员
            model.addAttribute("ismanager", 1);
        }else{
            model.addAttribute("ismanager", 0);
        }
        List<BankManager> bankManagerList = bankManagerService.findByBondCode(bondCode);
        model.addAttribute("bankManagerList", bankManagerList);
        return VIEW_PREFIX + "list";
    }

    //保存
    @ResponseBody
    @RequestMapping("/addSave")
    public RestResponse<String> addSave(@RequestBody BankManager bankManager) {
        RestResponse<String> result;

        try {
            int count = bankManagerService.insertBankManager(bankManager);
            if(count > 0){
                result = RestResponse.Success("", String.valueOf(bankManager.getId()));
            }else{
                result = RestResponse.Fail("添加失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("添加失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<String> editSave(@RequestBody BankManager bankManager) {
        RestResponse<String> result;

        try {
            int count = bankManagerService.updateBankManager(bankManager);
            if(count > 0){
                result = RestResponse.Success("");
            }else{
                result = RestResponse.Fail("修改失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("修改失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteSave/{id}", method = RequestMethod.GET)
    public RestResponse<String> deleteSave(@PathVariable int id){
        RestResponse<String> result;
        try{
            int resultStatus = bankManagerService.deleteById(id);
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
}
