package com.innodealing.bpms.controller;

import com.innodealing.bpms.appconfig.history.ProcessOperate;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.HandleMatterService;
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
@RequestMapping("/handle-matter")
public class HandleMatterController {
    private static final Logger logger = LoggerFactory.getLogger(HandleMatterController.class);
    @Autowired
    private HandleMatterService handleMatterService;
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
        return "app/bpms/handle-matter/index";
    }

    @ResponseBody
    @RequestMapping(value = "/findHandleMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findHandleMatters(@RequestBody(required = false) ReqData reqData) {
        return handleMatterService.findHandleMatters(reqData);
    }
    @RequestMapping(value="/handle-matter/{taskId}", method = RequestMethod.GET)
    public String getTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        MatterTask mt =  matterTaskService.findByProcessId(task.getProcessInstanceId());
        mt.setId(taskId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/handle-matter/handle-matter";
    }
    @RequestMapping(value="/handle-right-line-matter/{taskId}", method = RequestMethod.GET)
    public String getRightLineTask(@PathVariable String taskId,Model model){
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
        return "app/bpms/handle-matter/handle-right-line-matter";
    }

    @ProcessOperate(taskType = 2,mark="提交处理")
    @ResponseBody
    @RequestMapping(value="/handle-matter",method = RequestMethod.POST)
    public RestResponse<String> handlePass(@RequestBody MatterTask mt) {
        return handleMatterService.handlePass(mt);
    }

    @RequestMapping(value="/re-handle-matter/{taskId}", method = RequestMethod.GET)
    public String getReTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        MatterTask mt =  matterTaskService.findByProcessId(task.getProcessInstanceId());
        mt.setId(taskId);
        model.addAttribute("matterTask",mt);
        return "app/bpms/handle-matter/re-handle-matter";
    }
    @RequestMapping(value="/re-handle-right-line-matter/{taskId}", method = RequestMethod.GET)
    public String getReRightLineTask(@PathVariable String taskId,Model model){
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
        return "app/bpms/handle-matter/re-handle-right-line-matter";
    }
    @ProcessOperate(taskType = 2,mark="提交处理")
    @ResponseBody
    @RequestMapping(value="/re-handle-matter",method = RequestMethod.POST)
    public RestResponse<String> reHandlePass(@RequestBody MatterTask mt) {
        return handleMatterService.handlePass(mt);
    }
    @ProcessOperate(taskType = 2,mark="提交处理")
    @ResponseBody
    @RequestMapping(value="/handle-right-line-matter",method = RequestMethod.POST)
    public RestResponse<String> handleRightLinePass(@RequestBody MatterTask mt) {
        return handleMatterService.handlePass(mt);
    }
    @ProcessOperate(taskType = 2,mark="提交处理")
    @ResponseBody
    @RequestMapping(value="/re-handle-right-line-matter",method = RequestMethod.POST)
    public RestResponse<String> reHandleRightLinePass(@RequestBody MatterTask mt) {
        return handleMatterService.handlePass(mt);
    }
    @ResponseBody
    @RequestMapping(value="/update-notice-flag/{processId}/{flag}",method = RequestMethod.POST)
    public RestResponse<String> updateNoticeFlag(@PathVariable String processId,@PathVariable int flag) {
        return handleMatterService.updateNoticeFlag(processId,flag);
    }
    @RequestMapping(value = "/info/{taskId}/{pId}/{orderIndex}/{status}", method = RequestMethod.GET)
    public String info(Model model, @PathVariable String taskId,@PathVariable String pId,@PathVariable int orderIndex,@PathVariable int status) {
        model.addAttribute("taskId", taskId);
        model.addAttribute("pId", pId);
        model.addAttribute("orderIndex", orderIndex);
        model.addAttribute("status", status);
        return "app/bpms/handle-matter/info";
    }
}
