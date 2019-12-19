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
@RequestMapping("/manager-trace-matter")
public class ManagerTraceMatterController {
    private static final Logger logger = LoggerFactory.getLogger(ManagerTraceMatterController.class);
    @Autowired
    private ManagerTraceMatterService managerTraceMatterService;
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
        List<User> userList = userService.findByRoleCode(ConstantUtil.MANAGER_ROLE);
        model.addAttribute("userList", userList);
        return "app/bpms/manager-trace-matter/index";
    }

    @ResponseBody
    @RequestMapping(value = "/findManagerTraceMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findManagerTraceMatters(@RequestBody(required = false) ReqData reqData) {
        return managerTraceMatterService.findManagerTraceMatters(reqData);
    }

    @RequestMapping(value="/view-matter/{taskId}", method = RequestMethod.GET)
    public String viewTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        MatterTask mt =  matterTaskService.findByProcessId(task.getProcessInstanceId());
        mt.setId(taskId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/manager-trace-matter/view-matter";
    }

    @RequestMapping(value="/view-right-line-matter/{taskId}", method = RequestMethod.GET)
    public String viewRightLineTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(task.getProcessInstanceId());
        model.addAttribute("matterTasks",mts);
        MatterTask matterTask = null;
        for(MatterTask mt:mts){
            if(taskId.equals(mt.getId())){
                matterTask = mt;
            }
        }
        model.addAttribute("matterTask",matterTask);
        return "app/bpms/manager-trace-matter/view-right-line-matter";
    }
}
