package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Module;
import com.innodealing.bpms.model.Permission;
import com.innodealing.bpms.model.Role;
import com.innodealing.bpms.service.ModuleService;
import com.innodealing.bpms.service.PermissionService;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.commons.http.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    ModuleService moduleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @RequestMapping(value="/index")
    public String index(Model model){
        return "app/bpms/role/index";
    }
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model){
        List<Role> roleList = roleService.findAll();
        model.addAttribute("roleList", roleList);
        return "app/bpms/role/add";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable Integer id){
        Role role = roleService.findById(id);
        model.addAttribute("role",role);
        List<Role> roleList = roleService.findAll();
        model.addAttribute("roleList", roleList);
        return "app/bpms/role/edit";
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/findByPage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findByPage(@RequestBody(required = false) ReqData reqData) {
        PageInfo<Role> page = null;
        try {
            page = roleService.findByPage(reqData);
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
    public RestResponse<String> addSave(Role role) {
        RestResponse<String> result;
        try {
            roleService.save(role);
            result = RestResponse.Success("添加成功", "");
        }catch (DuplicateKeyException d){
            d.printStackTrace();
            result = RestResponse.Fail("提交失败,角色码已存在", "");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result = RestResponse.Fail("提交失败", "");
        }
        return result;
    }
    @ResponseBody
    @RequestMapping("/editSave")
    public RestResponse<String> editSave(Role role) {
        RestResponse<String> result;
        try {
            roleService.update(role);
            result = RestResponse.Success("修改成功", "");
        }catch (DuplicateKeyException d){
            d.printStackTrace();
            result = RestResponse.Fail("提交失败,角色码已存在", "");
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
            roleService.delete(ids);
            result = RestResponse.Success( "删除成功", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            result = RestResponse.Fail( "删除失败", "");
        }
        return result;
    }
    @RequestMapping(value = "/editRolePermission/{id}", method = RequestMethod.GET)
    public String editRolePermission(Model model, @PathVariable Integer id){
        Role role = roleService.findById(id);
        model.addAttribute("role",role);
        return "app/bpms/role/rolepermission";
    }

    @ResponseBody
    @RequestMapping("/saveRolePermission/{roleId}/{permissionIds}")
    public RestResponse<String> delete(@PathVariable int roleId,@PathVariable int[] permissionIds) {
        RestResponse<String> result;
        try {
            roleService.updateRolePremission(roleId,permissionIds);
            result = RestResponse.Success( "保存成功", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            result = RestResponse.Fail( "保存失败", "");
        }
        return result;
    }
}
