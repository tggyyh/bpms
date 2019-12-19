package com.innodealing.bpms.controller;

import com.innodealing.bpms.appconfig.history.ProcessOperate;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.ConfirmMatterService;
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
@RequestMapping("/confirm-matter")
public class ConfirmMatterController {
    private static final Logger logger = LoggerFactory.getLogger(ConfirmMatterController.class);
    @Autowired
    private ConfirmMatterService confirmMatterService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private UserService userService;
    @Autowired
    IdentityService identityService;
    @Autowired
    private MatterTaskService matterTaskService;

    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(Model model){
        List<User> userList = userService.findByRoleCode(ConstantUtil.MANAGER_ROLE);
        model.addAttribute("userList", userList);
        return "app/bpms/confirm-matter/index";
    }

    @ResponseBody
    @RequestMapping(value = "/findConfirmMatters", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> findConfirmMatters(@RequestBody(required = false) ReqData reqData) {
        return confirmMatterService.findConfirmMatters(reqData);
    }

    @RequestMapping(value="/{taskId}", method = RequestMethod.GET)
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
        return "app/bpms/confirm-matter/confirm";
    }

    @RequestMapping(value="/right-line/{taskId}", method = RequestMethod.GET)
    public String getRightLineTask(@PathVariable String taskId,Model model){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (null == task) {
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            model.addAttribute("assignee",hisTask.getAssignee());
            return "app/bpms/common-process/error";
        }
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(task.getProcessInstanceId());
        model.addAttribute("matterTasks",mts);
        return "app/bpms/confirm-matter/confirm-right-line";
    }

    @ProcessOperate(taskType = 1,type=1,mark="确认事项")
    @ResponseBody
    @RequestMapping(value="/confirm",method = RequestMethod.POST)
    public RestResponse<String> confirmPass(@RequestBody MatterTask mt) {
        return confirmMatterService.confirmPass(mt);
    }

    @ProcessOperate(taskType = 1,type=3,mark="确认事项")
    @ResponseBody
    @RequestMapping(value="/batch-confirm/{matterIds}",method = RequestMethod.POST)
    public RestResponse<List<String>> batchConfirm(@PathVariable String[] matterIds) {
        return confirmMatterService.batchConfirm(matterIds);
    }

    //取消流程
    @ResponseBody
    @RequestMapping(value="/cancel-process/{processId}",method = RequestMethod.POST)
    public RestResponse<String> cancelProcess(@PathVariable String processId) {
        return confirmMatterService.cancelProcess(processId);
    }

    @ProcessOperate(taskType = 1,type=2,mark="确认事项")
    @ResponseBody
    @RequestMapping(value="/confirm-right-line",method = RequestMethod.POST)
    public RestResponse<String> confirmRightLinePass(@RequestBody List<MatterTask> mts) {
        return confirmMatterService.confirmRightLinePass(mts);
    }

    //取消流程
    @ResponseBody
    @RequestMapping(value="/cancel-right-line-process/{processId}",method = RequestMethod.POST)
    public RestResponse<String> cancelRightLineProcess(@PathVariable String processId) {
        List<MatterTask> mts =  matterTaskService.findRightLineByProcessId(processId);
        return confirmMatterService.cancelRightLineProcess(mts);
    }

    @ResponseBody
    @RequestMapping(value="/batch-cancel/{matterIds}",method = RequestMethod.POST)
    public RestResponse<String> batchCancel(@PathVariable String[] matterIds) {
        return confirmMatterService.batchCancel(matterIds);
    }
    //删除事项
    @ResponseBody
    @RequestMapping(value="/batch-delete/{matterIds}",method = RequestMethod.POST)
    public RestResponse<String> batchDelete(@PathVariable String[] matterIds) {
        return confirmMatterService.batchDelete(matterIds);
    }
}
