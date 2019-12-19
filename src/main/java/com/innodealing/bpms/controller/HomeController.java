package com.innodealing.bpms.controller;

import com.innodealing.bpms.model.Module;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.ModuleService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.commons.http.RestResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhou on 2017/1/3.
 */
@Controller
//@RequestMapping(value = {"/"})
public class HomeController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = {"/","/login"}, method = RequestMethod.GET)
    public String login(Model model) {
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        logger.info("用户[" + subject + "]登录认证通过");
        model.addAttribute("userModule", getMenu(id));

        model.addAttribute("user", userService.findById(id));
        return "index";
    }

    @RequestMapping({"/home"})
    public String home(Model model) {
        //获取登录用户信息
        Subject subject = SecurityUtils.getSubject();
        String id = (String) subject.getPrincipals().getPrimaryPrincipal();
        User user = userService.findById(id);
        model.addAttribute("user", user);

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        model.addAttribute("nowDate", df.format(new Date()));

        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        model.addAttribute("week", dateFm.format(date));

        return "home";
    }

    private List<Module> getMenu(String userId) {
        List moduleList = moduleService.getModuleByUserId(userId);
        return moduleList;
    }

}