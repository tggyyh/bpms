package com.innodealing.bpms.controller;

import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.ActDemo;
import com.innodealing.bpms.model.ProcessInstanceDemo;
import com.innodealing.bpms.model.Resume;
import com.innodealing.bpms.model.TaskDemo;
import com.innodealing.bpms.service.RoleService;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/qj-process")
public class QJProcessController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
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

    @RequestMapping(value="/qj")
    public String de(Model model){
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        model.addAttribute("applicantName",account);
        return "app/bpms/qjprocess/add";
    }
    @RequestMapping(value="/qjHis",method = RequestMethod.GET)
    public String qjHis(Model model){
        return "app/bpms/qjprocess/qjhis";
    }
    @RequestMapping(value="/png",method = RequestMethod.GET)
    public String png(Model model){
        return "app/bpms/qjprocess/png";
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/qjHis", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> qjHisPage(@RequestBody(required = false) ReqData reqData) {
        PageInfo<Task> page = null;
        List<ProcessInstanceDemo> processDemos = new ArrayList();
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        List<HistoricProcessInstance> hisProcesss = historyService.createHistoricProcessInstanceQuery()
                .startedBy(account).orderByProcessInstanceStartTime().desc().list();
        Map<String, Object> map = new HashMap();
        for(HistoricProcessInstance process :hisProcesss){
            ProcessInstanceDemo processInstanceDemo = new ProcessInstanceDemo();
            processInstanceDemo.setId(process.getId());
            processInstanceDemo.setName(process.getName());
            processInstanceDemo.setCreateTime(process.getStartTime());
            processInstanceDemo.setEndTime(process.getEndTime());
            processInstanceDemo.setStartUser(process.getStartUserId());
            processDemos.add(processInstanceDemo);
        }
        map.put("total", processDemos.size());
        map.put("rows", processDemos);
        return map;
    }

    @RequestMapping(value="/sp")
    public String sp(Model model){
        return "app/bpms/qjprocess/sp";
    }
    @RequestMapping(value="/hrSp")
    public String hrSp(Model model){
        return "app/bpms/qjprocess/hrsp";
    }

    @RequestMapping(value="/taskHis")
    public String taskHis(Model model){
        return "app/bpms/qjprocess/taskhis";
    }

    @ResponseBody
    @RequestMapping(value="/addSave",method = RequestMethod.POST)
    public RestResponse<String> saveDetail(@RequestBody Resume resume) {
        RestResponse<String> result =null;
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("applicantName", resume.getApplicantName());
            variables.put("description", resume.getDescription());
            Subject subject = SecurityUtils.getSubject();
            String account = (String) subject.getPrincipal();
            identityService.setAuthenticatedUserId(account);
            ProcessInstance processInstance= runtimeService.startProcessInstanceByKey("myProcess_1", variables);
            result = RestResponse.Success("申请成功", "");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            result = RestResponse.Fail("申请失败", "");
        }
        return result;
    }
    //查询
    @ResponseBody
    @RequestMapping(value = "/tasklist", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> tasklist(@RequestBody(required = false) ReqData reqData) {
        PageInfo<Task> page = null;
        List<TaskDemo> taskDemos = new ArrayList();
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        List<String> codelist = roleService.findByUser(account);
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(account)
                .orderByTaskCreateTime()
                .asc()
                .list();
        List<Task> tasks1 = taskService.createTaskQuery()
                .taskCandidateGroupIn(codelist)
                .orderByTaskCreateTime()
                .asc()
                .list();
        tasks.addAll(tasks1);
        Map<String, Object> map = new HashMap();
        for(Task task : tasks ){
            TaskDemo taskDemo = new TaskDemo();
            taskDemo.setId(task.getId());
            taskDemo.setName(task.getName());
            taskDemo.setCreateTime(task.getCreateTime());
            taskDemo.setClaimTime(task.getClaimTime());
            taskDemo.setProcessInstanceId(task.getProcessInstanceId());
            taskDemos.add(taskDemo);
        }
        map.put("total", tasks.size());
        map.put("rows", taskDemos);
        return map;
    }

    //查询
    @ResponseBody
    @RequestMapping(value = "/histasklist", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> histasklist(@RequestBody(required = false) ReqData reqData) {
        PageInfo<Task> page = null;
        List<TaskDemo> taskDemos = new ArrayList();
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        List<HistoricTaskInstance> hisTasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(account)
                .finished()
                .orderByHistoricTaskInstanceEndTime().desc().list();
        Map<String, Object> map = new HashMap();
        for(HistoricTaskInstance task :hisTasks){
            TaskDemo taskDemo = new TaskDemo();
            taskDemo.setId(task.getId());
            taskDemo.setName(task.getName());
            taskDemo.setCreateTime(task.getCreateTime());
            taskDemo.setClaimTime(task.getClaimTime());
            taskDemo.setEndTime(task.getEndTime());
            taskDemo.setProcessInstanceId(task.getProcessInstanceId());
            taskDemos.add(taskDemo);
        }
        map.put("total", hisTasks.size());
        map.put("rows", taskDemos);
        return map;
    }

    @RequestMapping(value="/task/{taskId}",method = RequestMethod.GET)
    public String task(@PathVariable String taskId,Model model) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String applicantName = (String) runtimeService.getVariable(task.getExecutionId(), "applicantName");
        String description = (String) runtimeService.getVariable(task.getExecutionId(), "description");
        Integer checkResult = (Integer) runtimeService.getVariable(task.getExecutionId(), "checkResult");
        model.addAttribute("applicantName",applicantName);
        model.addAttribute("description",description);
        model.addAttribute("taskId",taskId);
        model.addAttribute("checkResult",checkResult);
        return "app/bpms/qjprocess/task";
    }

    @RequestMapping(value="/hrTask/{taskId}",method = RequestMethod.GET)
    public String hrTask(@PathVariable String taskId,Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String applicantName = (String) runtimeService.getVariable(task.getExecutionId(), "applicantName");
        String description = (String) runtimeService.getVariable(task.getExecutionId(), "description");
        Integer checkResult = (Integer) runtimeService.getVariable(task.getExecutionId(), "checkResult");
        model.addAttribute("applicantName",applicantName);
        model.addAttribute("description",description);
        model.addAttribute("taskId",taskId);
        model.addAttribute("checkResult",checkResult);
        return "app/bpms/qjprocess/hrtask";
    }

    @RequestMapping(value="/hisTask/{processInstanceId}",method = RequestMethod.GET)
    public String hisTask(@PathVariable String processInstanceId,Model model) {

        String applicantName = (String) historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                .variableName("applicantName").singleResult().getValue();
        String description = (String) historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                .variableName("description").singleResult().getValue();
        Integer checkResult = (Integer) historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                .variableName("checkResult").singleResult().getValue();
        model.addAttribute("applicantName",applicantName);
        model.addAttribute("description",description);
        model.addAttribute("checkResult",checkResult);
        return "app/bpms/qjprocess/histask";
    }

    @ResponseBody
    @RequestMapping(value="/taskPass/{taskId}",method = RequestMethod.POST)
    public RestResponse<String> taskPass(@PathVariable String taskId,Model model) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();

        String account = (String) subject.getPrincipal();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null != task){
            String exId = task.getExecutionId();
            runtimeService.setVariable(exId,"checkResult",1);
            taskService.setAssignee(taskId,account);
            taskService.complete(taskId);
        }else{
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            return RestResponse.Success("任务已被"+hisTask.getAssignee()+"处理");
        }
        result = RestResponse.Success("处理成功", "");
        return result;
    }

    @ResponseBody
    @RequestMapping(value="/taskReject/{taskId}",method = RequestMethod.POST)
    public RestResponse<String> taskReject(@PathVariable String taskId,Model model) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null != task){
            String exId = task.getExecutionId();
            runtimeService.setVariable(exId,"checkResult",0);
            taskService.setAssignee(taskId,account);
            taskService.complete(taskId);
        }else{
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            return RestResponse.Success("任务已被"+hisTask.getAssignee()+"处理");
        }
        result = RestResponse.Success("处理成功", "");
        return result;
    }

    @RequestMapping(value="/process/{processId}",method = RequestMethod.GET)
    public String process(@PathVariable String processId,Model model) {
        model.addAttribute("processId",processId);
        return "app/bpms/qjprocess/processdetail";
    }
    @ResponseBody
    @RequestMapping(value = "/processDetail", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public Map<String, Object> processDetail(@RequestBody(required = false) ReqData reqData) {
        List<ActDemo> actDemos = new ArrayList();
        Map<String, Object> map = new HashMap();
        List<HistoricActivityInstance> acts = historyService.createHistoricActivityInstanceQuery().processInstanceId((String)reqData.get("processId"))
                .orderByHistoricActivityInstanceStartTime().asc().list();
        for(HistoricActivityInstance act:acts){
            ActDemo actDemo = new ActDemo();
            actDemo.setId(act.getId());
            actDemo.setName(act.getActivityName());
            actDemo.setCreateTime(act.getStartTime());
            actDemo.setEndTime(act.getEndTime());
            actDemo.setType(act.getActivityType());
            actDemo.setAssignee(act.getAssignee());
            actDemo.setProcessInstanceId(act.getProcessInstanceId());
            actDemos.add(actDemo);
        }
        map.put("total", actDemos.size());
        map.put("rows", actDemos);
        return map;
    }

    @ResponseBody
    @RequestMapping(value="/cancelProcess/{processId}",method = RequestMethod.POST)
    public RestResponse<String> cancelProcess(@PathVariable String processId,Model model) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        try{
            runtimeService.deleteProcessInstance(processId,account+"取消");

            result = RestResponse.Success("处理成功", "");
        }catch (ActivitiObjectNotFoundException e){
            result = RestResponse.Fail("流程已结束", "");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value="/hrTaskPass/{taskId}",method = RequestMethod.POST)
    public RestResponse<String> hrTaskPass(@PathVariable String taskId,Model model) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();

        String account = (String) subject.getPrincipal();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null != task){
            String exId = task.getExecutionId();
            runtimeService.setVariable(exId,"hrCheckResult",1);
            taskService.setAssignee(taskId,account);
            taskService.complete(taskId);
        }else{
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            return RestResponse.Success("任务已被"+hisTask.getAssignee()+"处理");
        }

        result = RestResponse.Success("处理成功", "");
        return result;
    }

    @ResponseBody
    @RequestMapping(value="/hrTaskReject/{taskId}",method = RequestMethod.POST)
    public RestResponse<String> hrTaskReject(@PathVariable String taskId,Model model) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String account = (String) subject.getPrincipal();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null != task){
            String exId = task.getExecutionId();
            runtimeService.setVariable(exId,"hrCheckResult",0);
            taskService.setAssignee(taskId,account);
            taskService.complete(taskId);
        }else{
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            return RestResponse.Success("任务已被"+hisTask.getAssignee()+"处理");
        }
        result = RestResponse.Success("处理成功", "");
        return result;
    }
}
