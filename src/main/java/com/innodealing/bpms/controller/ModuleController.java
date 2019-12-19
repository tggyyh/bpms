package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Module;
import com.innodealing.bpms.model.Permission;
import com.innodealing.bpms.service.ModuleService;
import com.innodealing.bpms.service.PermissionService;
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
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    ModuleService moduleService;
    @Autowired
    PermissionService permissionService;
    private static final Logger logger = LoggerFactory.getLogger(ModuleController.class);

    @RequestMapping(value="/index")
    public String index(Model model){
        return "app/bpms/module/index";
    }
    @ResponseBody
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<Module> findAll(){
        List<Module> modules = moduleService.findAll();
        return moduleService.convertToTree(modules);
    }

    @ResponseBody
    @RequestMapping(value = "/findAllAddPremission/{roleId}", method = RequestMethod.POST)
    public List<Module> findAllAddPremission(@PathVariable Integer roleId){
        List<Permission> permissionList = permissionService.findByRoleId(roleId);
        List<Module> modules = moduleService.findAll();
        List<Permission> permissions = permissionService.findAll();
        for(Permission permission:permissions){
            if(permissionList.contains(permission)){
                permission.setChecked(true);
            }
            for(Module module: modules){
                if(null == module.getPermissions()){
                    module.setPermissions(new ArrayList<Permission>());
                }
                if(module.getId()==permission.getModuleId()){
                    module.getPermissions().add(permission);
                }
            }
        }
        return moduleService.convertToTree(modules);
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model){
        List<Module> moduleList = moduleService.findAll();
        model.addAttribute("moduleList", moduleList);
        return "app/bpms/module/add";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable Integer id){
        Module module = moduleService.findById(id);
        model.addAttribute("module",module);
        List<Module> moduleList = moduleService.findAll();
        model.addAttribute("moduleList", moduleList);
        return "app/bpms/module/edit";
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/findByPage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findByPage(@RequestBody(required = false) ReqData reqData) {
        PageInfo<Module> page = null;
        try {
            page = moduleService.findByPage(reqData);
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
    public RestResponse<String> addSave(Module module) {
        RestResponse<String> result;
        try {
            moduleService.save(module);
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
    public RestResponse<String> editSave(Module module) {
        RestResponse<String> result;
        try {
            moduleService.update(module);
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
            moduleService.delete(ids);
            result = RestResponse.Success( "删除成功", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            result = RestResponse.Fail( "删除失败", "");
        }
        return result;
    }
}
