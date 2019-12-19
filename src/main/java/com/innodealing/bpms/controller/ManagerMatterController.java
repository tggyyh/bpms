package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.*;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manager-matter")
public class ManagerMatterController {
    private static final Logger logger = LoggerFactory.getLogger(ManagerMatterController.class);
    @Autowired
    private ManagerMatterService managerMatterService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RoleService roleService;
    @Autowired
    IdentityService identityService;
    @Autowired
    private MatterTaskService matterTaskService;
    @Autowired
    private UserService userService;

    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(Model model){
        return "app/bpms/manager-matter/index";
    }

    @RequestMapping(value="/undo-matter", method = RequestMethod.GET)
    public String undoMatter(Model model){
        List<User> userList = userService.findByRoleCode(ConstantUtil.MANAGER_ROLE);
        model.addAttribute("userList", userList);
        return "app/bpms/manager-matter/undo-matter";
    }
    @RequestMapping(value="/over-matter", method = RequestMethod.GET)
    public String overMatter(Model model){
        List<User> userList = userService.findByRoleCode(ConstantUtil.MANAGER_ROLE);
        model.addAttribute("userList", userList);
        return "app/bpms/manager-matter/over-matter";
    }

    //查询
    @ResponseBody
    @RequestMapping(value = "/findOverMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findOverMatters(@RequestBody(required = false) ReqData reqData) {
        return managerMatterService.findOverMatters(reqData);
    }

    @ResponseBody
    @RequestMapping(value = "/findManagerMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findHandleMatters(@RequestBody(required = false) ReqData reqData) {
        return managerMatterService.findManagerMatters(reqData);
    }

    @RequestMapping(value="/view-over-matter/{processId}", method = RequestMethod.GET)
    public String viewHandleOverTask(@PathVariable String processId,Model model){
        MatterTask mt =  matterTaskService.findByProcessId(processId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/manager-trace-matter/view-matter";
    }

    @RequestMapping(value="/view-over-matter/right-line/{processId}", method = RequestMethod.GET)
    public String viewHandleOverRightLineTask(@PathVariable String processId,Model model){
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(processId);
        MatterTask matterTask = null;
        for(MatterTask mt:mts){
            if(processId.equals(mt.getProcessId())){
                matterTask = mt;
            }
        }
        model.addAttribute("matterTask",matterTask);
        model.addAttribute("matterTasks",mts);
        return "app/bpms/manager-trace-matter/view-right-line-matter";
    }
}
