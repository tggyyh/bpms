package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Role;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.commons.http.RestResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value="/index")
    public String index(Model model){
        return "app/bpms/user/index";
    }

    @RequestMapping(value="/add")
//    @RequiresPermissions("user:add")
    public String add(Model model){
        model.addAttribute("roles",roleService.findAll());
        return "app/bpms/user/add";
    }
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable String id){
        User user = userService.findById(id);
        Role role = userService.findRoleById(id);
        model.addAttribute("user",user);
        model.addAttribute("role",role);
        model.addAttribute("roles",roleService.findAll());
        return "app/bpms/user/edit";
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/findByPage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findByPage(@RequestBody(required = false) ReqData reqData) {
        PageInfo<User> page = null;
        try {
            page = userService.findByPage(reqData);
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
    @RequestMapping("/editSave")
    public RestResponse<String> editSave(User user) {
        RestResponse<String> result;
        try {
            Subject subject = SecurityUtils.getSubject();
            String userId = (String) subject.getPrincipal();
            userService.update(user);
            result = RestResponse.Success("修改成功", "");
        }catch (DuplicateKeyException d){
            d.printStackTrace();
            result = RestResponse.Fail("提交失败,账号已存在", "");
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
            userService.delete(ids);
            result = RestResponse.Success( "删除成功", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            result = RestResponse.Fail( "删除失败", "");
        }
        return result;
    }
}
