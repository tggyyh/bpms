package com.innodealing.bpms.controller;

import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.InspectorTraceMatterService;
import com.innodealing.bpms.service.MatterTaskService;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.bpms.service.UserService;
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
@RequestMapping("/inspector-trace-matter")
public class InspectorTraceMatterController {
    private static final Logger logger = LoggerFactory.getLogger(InspectorTraceMatterController.class);
    @Autowired
    private InspectorTraceMatterService inspectorTraceMatterService;
    @Autowired
    private TaskService taskService;
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
        return "app/bpms/inspector-trace-matter/index";
    }

    @ResponseBody
    @RequestMapping(value = "/findInspectorTraceMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findInspectorTraceMatters(@RequestBody(required = false) ReqData reqData) {
        return inspectorTraceMatterService.findInspectorTraceMatters(reqData);
    }

    @RequestMapping(value="/{taskId}", method = RequestMethod.GET)
    public String viewTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        MatterTask mt =  matterTaskService.findByProcessId(task.getProcessInstanceId());
        mt.setId(taskId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/inspector-trace-matter/view-matter";
    }
    @RequestMapping(value="/right-line/{taskId}", method = RequestMethod.GET)
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
        return "app/bpms/inspector-trace-matter/view-right-line-matter";
    }

    @RequestMapping(value = "/info/{taskId}/{pId}/{orderIndex}", method = RequestMethod.GET)
    public String info(Model model, @PathVariable String taskId,@PathVariable String pId,@PathVariable int orderIndex) {
        model.addAttribute("taskId", taskId);
        model.addAttribute("pId", pId);
        model.addAttribute("orderIndex", orderIndex);
        return "app/bpms/inspector-trace-matter/info";
    }
}
