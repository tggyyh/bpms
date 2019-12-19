package com.innodealing.bpms.controller;

import com.innodealing.bpms.appconfig.history.ProcessOperate;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.CheckMatterService;
import com.innodealing.bpms.service.MatterTaskService;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.bpms.service.UserService;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
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
@RequestMapping("/check-matter")
public class CheckMatterController {
    private static final Logger logger = LoggerFactory.getLogger(CheckMatterController.class);
    @Autowired
    private CheckMatterService checkMatterService;
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
        return "app/bpms/check-matter/index";
    }

    @ResponseBody
    @RequestMapping(value = "/findCheckMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findCheckMatters(@RequestBody(required = false) ReqData reqData) {
        return checkMatterService.findCheckMatters(reqData);
    }


    @RequestMapping(value="/check-matter/{taskId}", method = RequestMethod.GET)
    public String getCheckTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        MatterTask mt =  matterTaskService.findByProcessId(task.getProcessInstanceId());
        mt.setId(taskId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/check-matter/check-matter";
    }
    @RequestMapping(value="/check-right-line-matter/{taskId}", method = RequestMethod.GET)
    public String getCheckRightLineTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(task.getProcessInstanceId());
        MatterTask matterTask = null;
        for(MatterTask mt:mts){
            if(taskId.equals(mt.getId())){
                matterTask = mt;
            }
        }
        model.addAttribute("matterTask",matterTask);
        model.addAttribute("matterTasks",mts);
        return "app/bpms/check-matter/check-right-line-matter";
    }
    @ProcessOperate(taskType = 3,mark="审核")
    @ResponseBody
    @RequestMapping(value="/check-matter",method = RequestMethod.POST)
    public RestResponse<String> checkPass(@RequestBody MatterTask mt) {
        return checkMatterService.checkPass(mt);
    }

    @RequestMapping(value="/re-check-matter/{taskId}", method = RequestMethod.GET)
    public String getReCheckTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        MatterTask mt =  matterTaskService.findByProcessId(task.getProcessInstanceId());
        mt.setId(taskId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/check-matter/re-check-matter";
    }

    @RequestMapping(value="/re-check-right-line-matter/{taskId}", method = RequestMethod.GET)
    public String getReCheckRightLineTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(task.getProcessInstanceId());
        MatterTask matterTask = null;
        for(MatterTask mt:mts){
            if(taskId.equals(mt.getId())){
                matterTask = mt;
            }
        }
        model.addAttribute("matterTask",matterTask);
        model.addAttribute("matterTasks",mts);
        return "app/bpms/check-matter/re-check-right-line-matter";
    }
    @ProcessOperate(taskType = 3,mark="审核")
    @ResponseBody
    @RequestMapping(value="/re-check-matter",method = RequestMethod.POST)
    public RestResponse<String> reCheckPass(@RequestBody MatterTask mt) {
        return checkMatterService.checkPass(mt);
    }
    @ProcessOperate(taskType = 3,mark="审核")
    @ResponseBody
    @RequestMapping(value="/check-right-line-matter",method = RequestMethod.POST)
    public RestResponse<String> checkRightLinePass(@RequestBody MatterTask mt) {
        return checkMatterService.checkPass(mt);
    }
    @ProcessOperate(taskType = 3,mark="审核")
    @ResponseBody
    @RequestMapping(value="/re-check-right-line-matter",method = RequestMethod.POST)
    public RestResponse<String> reCheckRightLinePass(@RequestBody MatterTask mt) {
        return checkMatterService.checkPass(mt);
    }

    @RequestMapping(value = "/info/{taskId}/{pId}/{orderIndex}/{status}", method = RequestMethod.GET)
    public String info(Model model, @PathVariable String taskId,@PathVariable String pId,@PathVariable int orderIndex,@PathVariable int status) {
        model.addAttribute("taskId", taskId);
        model.addAttribute("pId", pId);
        model.addAttribute("orderIndex", orderIndex);
        model.addAttribute("status", status);
        return "app/bpms/check-matter/info";
    }
}
