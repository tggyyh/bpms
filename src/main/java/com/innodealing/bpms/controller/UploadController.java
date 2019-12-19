package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.mail.MailService;
import com.innodealing.bpms.model.Bond;
import com.innodealing.bpms.model.EmailSendMessage;
import com.innodealing.bpms.model.Upload;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.BondService;
import com.innodealing.bpms.service.UploadService;
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

import java.util.List;

@Controller
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    UploadService uploadService;
    @Autowired
    BondService bondService;
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private final static String VIEW_PREFIX = "app/bpms/upload/";

    @RequestMapping(value="/addCompany/{companyName:.+}")
    public String addCompany(Model model, @PathVariable String companyName){
        model.addAttribute("uploadType", 0);
        model.addAttribute("uploadCompanyName", companyName);
        model.addAttribute("uploadBondCode", "");
        model.addAttribute("uploadBondName", "");
        return VIEW_PREFIX + "add";
    }

    @RequestMapping(value="/addBond/{bondCode:.+}")
    public String addBond(Model model, @PathVariable String bondCode){
        String bondName = "", companyName = "";
        Bond bond = bondService.findBondByCode(bondCode);
        if(null!=bond){
            if(null!=bond.getShortname() && bond.getShortname()!=""){
                bondName = bond.getShortname();
            }
            if(null!=bond.getCompanyName() && bond.getCompanyName()!=""){
                companyName = bond.getCompanyName();
            }
        }
        if(null!=bond && null!=bond.getShortname() && bond.getShortname()!=""){
            bondName = bond.getShortname();
        }

        model.addAttribute("uploadType", 1);
        model.addAttribute("uploadCompanyName", companyName);
        model.addAttribute("uploadBondCode", bondCode);
        model.addAttribute("uploadBondName", bondName);
        return VIEW_PREFIX + "add";
    }

    @RequestMapping(value="/edit/{id}")
    public String edit(Model model, @PathVariable int id){
        Upload upload = uploadService.findById(id);
        model.addAttribute("upload", upload);
        return VIEW_PREFIX + "edit";
    }

    @ResponseBody
    @RequestMapping(value = "/findBondInfo/{companyName:.+}", method = RequestMethod.GET)
    public List<Bond> findBondInfo(@PathVariable String companyName){
        List<Bond> bondList = bondService.findBondInfoByCompany(companyName);
        return bondList;
    }

    //保存
    @ResponseBody
    @RequestMapping("/addSave")
    public RestResponse<String> addSave(@RequestBody Upload upload) {
        RestResponse<String> result;

        try {
            //获取登录用户信息
            Subject subject = SecurityUtils.getSubject();
            String id = (String) subject.getPrincipals().getPrimaryPrincipal();
            User user = userService.findById(id);
            String roleCode = "";
            if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
                roleCode = ConstantUtil.MANAGER_ROLE;
                upload.setStatus(1);
            }else if(subject.hasRole(ConstantUtil.INSPECTOR_ROLE)){
                roleCode = ConstantUtil.INSPECTOR_ROLE;
                upload.setStatus(0);
            }

            upload.setUserId(user.getId());
            upload.setAccount(user.getAccountNumber());
            upload.setUserName(user.getName());
            upload.setRoleCode(roleCode);

            int count = uploadService.insertUpload(upload);
            if(count > 0){
                result = RestResponse.Success("");
            }else{
                result = RestResponse.Fail("添加失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("添加失败","");
        }

        return result;
    }

    //保存
    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<String> editSave(@RequestBody Upload upload) {
        RestResponse<String> result;

        try {
            //获取登录用户信息
            Subject subject = SecurityUtils.getSubject();
            String id = (String) subject.getPrincipals().getPrimaryPrincipal();
            User user = userService.findById(id);
            String roleCode = "";
            if(subject.hasRole(ConstantUtil.MANAGER_ROLE)){
                roleCode = ConstantUtil.MANAGER_ROLE;
                upload.setStatus(1);
            }else if(subject.hasRole(ConstantUtil.INSPECTOR_ROLE)){
                roleCode = ConstantUtil.INSPECTOR_ROLE;
                upload.setStatus(0);
            }

            upload.setUserId(user.getId());
            upload.setAccount(user.getAccountNumber());
            upload.setUserName(user.getName());
            upload.setRoleCode(roleCode);

            int count = uploadService.uploadUpload(upload);
            if(count > 0){
                result = RestResponse.Success("");
            }else{
                result = RestResponse.Fail("编辑失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("添加失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deleteUpload", method = RequestMethod.POST)
    public RestResponse<String> deleteUpload(@RequestBody int id){
        RestResponse<String> result;
        try{
            Upload upload = new Upload();
            upload.setId(id);
            int resultStatus = uploadService.deleteUpload(upload);
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
    @RequestMapping(value = "/deleteUploadAttachmentById", method = RequestMethod.POST)
    public RestResponse<String> deleteUploadAttachmentById(@RequestBody int id){
        RestResponse<String> result;
        try{
            int resultStatus = uploadService.deleteUploadAttachmentById(id);
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

    //审核
    @ResponseBody
    @RequestMapping("/auditUpload")
    public RestResponse<String> auditUpload(@RequestBody int id) {
        RestResponse<String> result;

        try {
            Upload upload = new Upload();
            upload.setId(id);
            upload.setStatus(0);

            int count = uploadService.auditUpload(upload);
            if(count > 0){
                result = RestResponse.Success("");
            }else{
                result = RestResponse.Fail("审核失败","");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return RestResponse.Fail("审核失败","");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/repellentUpload", method = RequestMethod.POST)
    public RestResponse<String> repellentUpload(@RequestBody Upload upload){
        RestResponse<String> result;
        try{
            int resultStatus = uploadService.deleteUpload(upload);

            if(resultStatus>0){
                result = RestResponse.Success( "拒绝成功", "");

                //发邮件
                User user = userService.findById(upload.getUserId());
                if(null!=user && null!=user.getEmail() && user.getEmail()!=""){
                    String[] toUser = new String[]{user.getEmail()};
                    EmailSendMessage message = new EmailSendMessage();
                    message.setTo(toUser);
                    String subject = "";
                    if(upload.getType()==0){
                        subject = "（" + (null==upload.getCompanyName()?"":upload.getCompanyName()) + "—" + (null==upload.getDescription()?"":upload.getDescription()) + "）上传材料被驳回通知";
                    }else if(upload.getType()==1){
                        subject = "（" + (null==upload.getBondName()?"":upload.getBondName()) + "—" + (null==upload.getDescription()?"":upload.getDescription()) + "）上传材料被驳回通知";
                    }
                    message.setSubject(subject);
                    message.setText("在存续期管理系统中上传的材料已被拒绝，请与督导人员进行确认");
                    mailService.sendMessage(message);
                }
            }else{
                result = RestResponse.Fail("拒绝失败","");
            }
        }catch(Exception ex){
            logger.info("拒绝失败:" + ex.getMessage());
            result = RestResponse.Fail("拒绝失败","");
        }
        return result;
    }
}
