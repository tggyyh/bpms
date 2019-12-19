package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Permission;
import com.innodealing.bpms.service.PermissionService;
import com.innodealing.commons.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    PermissionService permissionService;
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @RequestMapping(value="/index/{moduleId}")
    public String index(Model model,@PathVariable Integer moduleId){
        model.addAttribute("moduleId", moduleId);
        return "app/bpms/permission/index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model){
        List<Permission> permissionList = permissionService.findAll();
        model.addAttribute("permissionList", permissionList);
        return "app/bpms/permission/add";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable Integer id){
        Permission permission = permissionService.findById(id);
        model.addAttribute("permission",permission);
        List<Permission> permissionList = permissionService.findAll();
        model.addAttribute("permissionList", permissionList);
        return "app/bpms/permission/edit";
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/findByPage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findByPage(@RequestBody(required = false) ReqData reqData) {
        PageInfo<Permission> page = null;
        try {
            page = permissionService.findByPage(reqData);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败："+e.getMessage());
        }
        Map<String, Object> map = new HashMap();
        map.put("total", page.getTotal());
        map.put("rows", page.getList());
        return map;
    }

    @ResponseBody
    @RequestMapping("/addSave")
    public RestResponse<String> addSave(Permission permission) {
        RestResponse<String> result;
        try {
            permissionService.save(permission);
            result = RestResponse.Success("添加成功", "");
        }catch (DuplicateKeyException d){
            d.printStackTrace();
            result = RestResponse.Fail("提交失败,模块码已存在", "");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result = RestResponse.Fail("提交失败", "");
        }
        return result;
    }
    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<String> editSave(Permission permission) {
        RestResponse<String> result;
        try {
            permissionService.update(permission);
            result = RestResponse.Success("修改成功", "");
        }catch (DuplicateKeyException d){
            d.printStackTrace();
            result = RestResponse.Fail("提交失败,模块码已存在", "");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result = RestResponse.Fail( "修改失败", "");
        }
        return result;
    }

    @ResponseBody
    @RequestMapping("/delete/{ids}")
    public RestResponse<String> delete(@PathVariable int[] ids) {
        RestResponse<String> result;
        try {
            permissionService.delete(ids);
            result = RestResponse.Success( "删除成功", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            result = RestResponse.Fail( "删除失败", "");
        }
        return result;
    }
}
