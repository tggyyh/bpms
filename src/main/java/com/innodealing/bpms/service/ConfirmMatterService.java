package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.appconfig.history.CompareObject;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.common.model.Status;
import com.innodealing.bpms.controller.CheckMatterController;
import com.innodealing.bpms.mapper.MatterTaskMapper;
import com.innodealing.bpms.mapper.SurviveProcessMapper;
import com.innodealing.bpms.model.*;
import com.innodealing.commons.http.RestResponse;
import jdk.nashorn.internal.runtime.ECMAException;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.activiti.engine.HistoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("confirmMatterService")
public class ConfirmMatterService {
    private static final Logger logger = LoggerFactory.getLogger(ConfirmMatterService.class);
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RoleService roleService;
    @Autowired
    IdentityService identityService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private ProcessInfoService processInfoService;
    @Autowired
    private SurviveProcessMapper surviveProcessMapper;
    @Autowired
    private MatterTaskService matterTaskService;

    @Autowired
    MatterTaskMapper matterTaskMapper;

    public RestResponse<String> confirmPass(MatterTask mt) {
        RestResponse<String> result =null;
        try {
            Subject subject = SecurityUtils.getSubject();
            String userId = (String) subject.getPrincipal();
            String taskId = mt.getId();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != task) {
                MatterTask mtOld =  matterTaskService.findByProcessId(task.getProcessInstanceId());
                String exId = task.getExecutionId();
                taskService.setAssignee(taskId, userId);
                taskService.complete(taskId);
                processService.updateData(mt);
                List<String> list = CompareObject.compare(mtOld.getProcessInfo(),mt.getProcessInfo());
                boolean flag = false;
                if(!CollectionUtils.isEmpty(list)){
                    flag = true;
                }
                processService.sendMail(mt.getProcessInfo(),flag);
            } else {
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                return RestResponse.Fail("任务已被" + hisTask.getAssignee() + "处理");
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return RestResponse.Fail("任务处理失败："+e.getMessage());
        }
        result = RestResponse.Success("处理成功");
        return  result;
    }

    public RestResponse<String> cancelProcess(String processId) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String userId = (String) subject.getPrincipal();
        try{
            runtimeService.deleteProcessInstance(processId,userId+"取消");
            processService.updateSurviveProcess(ConstantUtil.PROCESS_CANCEL_STATUS,processId);
            ProcessInfo pi = new ProcessInfo();
            pi.setProcessId(processId);
            pi.setStatus(Status.CANCEL.getValue());
            processService.updateProcessInfoStatus(pi);
            result = RestResponse.Success("处理成功", "");
        }catch (ActivitiObjectNotFoundException e){
            logger.error(e.getMessage(),e);
            result = RestResponse.Fail("事项已结束", "");
        }

        return result;
    }

    public Map<String,Object> findConfirmMatters(ReqData reqData) {
        Integer pageNum = ConstantUtil.PAGE_NUM;
        Integer pageSize = ConstantUtil.PAGE_SIZE;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        List<List<MatterTask>> tasks = new ArrayList();
        PageInfo<MatterTask> page;
        PageHelper.offsetPage((pageNum-1)*pageSize, pageSize);
        List<Company> companies = matterTaskMapper.findConfirmCompany(reqData);
        for(Company company:companies){
            List<MatterTask> matterTasks = new ArrayList();
            reqData.put("companyName",company.getName());
            matterTasks = matterTaskMapper.findConfirmTasks(reqData);
            tasks.add(matterTasks);
        }
        page = new PageInfo(companies);
        Map<String, Object> map = new HashMap();
        map.put("total", page.getTotal());
        map.put("rows", tasks);
        return map;
    }

    @Transactional
    public RestResponse<String> confirmRightLinePass(List<MatterTask> mts) {
        RestResponse<String> result =null;
        try {
            Subject subject = SecurityUtils.getSubject();
            String userId = (String) subject.getPrincipal();
            String taskId = mts.get(0).getId();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != task) {
                List<MatterTask> mtsOld =  matterTaskService.findRightLineByProcessId(task.getProcessInstanceId());
                boolean b = false;
                for(MatterTask mt:mts) {
                    if(!b){
                        String processId = mt.getProcessId();
                        for(MatterTask mtOld:mtsOld){
                            if(mtOld.getProcessId().equals(processId)){
                                List<String> list = CompareObject.compare(mtOld.getProcessInfo(),mt.getProcessInfo());
                                if(!CollectionUtils.isEmpty(list)){
                                    b = true;
                                    break;
                                }
                            }
                        }
                    }
                    String id = mt.getId();
                    Task subTask = taskService.createTaskQuery().taskId(id).singleResult();
                    taskService.setAssignee(id, userId);
                    taskService.complete(id);
                    processService.updateData(mt);
                }
                processService.sendMail(mts.get(0).getProcessInfo(),b);
            } else {
                HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                return RestResponse.Fail("任务已被" + hisTask.getAssignee() + "处理");
            }

        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return RestResponse.Fail("任务处理失败："+e.getMessage());
        }
        result = RestResponse.Success("处理成功");
        return  result;
    }

    public RestResponse<String> cancelRightLineProcess(List<MatterTask> mts) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String userId = (String) subject.getPrincipal();
        try{
            for(MatterTask mt:mts) {
                String processId = mt.getProcessId();
                runtimeService.deleteProcessInstance(processId, userId + "取消");
                processService.updateSurviveProcess(ConstantUtil.PROCESS_CANCEL_STATUS, processId);
                ProcessInfo pi = new ProcessInfo();
                pi.setProcessId(processId);
                pi.setStatus(Status.CANCEL.getValue());
                processService.updateProcessInfoStatus(pi);
            }
            result = RestResponse.Success("处理成功", "");
        }catch (ActivitiObjectNotFoundException e){
            logger.error(e.getMessage(),e);
            result = RestResponse.Fail("事项已结束", "");
        }
        return result;
    }

    public RestResponse<List<String>> batchConfirm(String[] matterIds) {
        RestResponse<List<String>> result =null;
        List<String> pIds = new ArrayList();
        try {
            Subject subject = SecurityUtils.getSubject();
            String userId = (String) subject.getPrincipal();

            for(String taskId:matterIds) {
                Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                if (null != task) {
                    ProcessInfo pi = processInfoService.findByTaskId(taskId);
                    int orderIndex = surviveProcessMapper.findOrderIndexByProcessId(pi.getProcessId());
                    if(orderIndex>0){
                        List<MatterTask> mts = matterTaskMapper.findRightLineByProcessId(pi.getProcessId());
                        for(MatterTask mt:mts) {
                            String id = mt.getId();
                            Task subTask = taskService.createTaskQuery().taskId(id).singleResult();
                            taskService.setAssignee(id, userId);
                            taskService.complete(id);
                            ProcessInfo piSub = processInfoService.findByTaskId(id);
                            piSub.setStatus(2);
                            processService.updateProcessInfoStatus(piSub);
                            pIds.add(piSub.getProcessId());
                        }
                        pi.setStatus(2);
                        processService.sendMail(pi,false);
                    }else {
                        taskService.setAssignee(taskId, userId);
                        taskService.complete(taskId);
                        pi.setStatus(2);
                        processService.updateProcessInfoStatus(pi);
                        pIds.add(pi.getProcessId());
                        processService.sendMail(pi,false);
                    }
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return RestResponse.Fail("任务处理失败："+e.getMessage(),null);
        }
        result = RestResponse.Success("处理成功",pIds);
        return  result;
    }

    public RestResponse<String> batchCancel(String[] matterIds) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String userId = (String) subject.getPrincipal();
        for (String mId : matterIds) {
            ProcessInfo pi = processInfoService.findByTaskId(mId);
            String processId = pi.getProcessId();
            int orderIndex = surviveProcessMapper.findOrderIndexByProcessId(processId);
            if(orderIndex>0){
                try{
                    List<MatterTask> mts = matterTaskMapper.findRightLineByProcessId(processId);
                    for(MatterTask mt:mts) {
                        String pIdSub = mt.getProcessId();
                        runtimeService.deleteProcessInstance(pIdSub, userId + "取消");
                        processService.updateSurviveProcess(ConstantUtil.PROCESS_CANCEL_STATUS, pIdSub);
                        ProcessInfo subProcessInfo = mt.getProcessInfo();
                        subProcessInfo.setStatus(Status.CANCEL.getValue());
                        processService.updateProcessInfoStatus(subProcessInfo);
                    }
                }catch (ActivitiObjectNotFoundException e){
                    logger.info("流程已取消：" + processId);
                }

            }else {
                try {
                    runtimeService.deleteProcessInstance(processId, userId + "取消");
                    processService.updateSurviveProcess(ConstantUtil.PROCESS_CANCEL_STATUS, processId);
                    pi.setStatus(Status.CANCEL.getValue());
                    processService.updateProcessInfoStatus(pi);
                } catch (ActivitiObjectNotFoundException e) {
                    logger.info("流程已取消：" + processId);
                }
            }
        }
        result = RestResponse.Success("处理成功");
        return result;
    }

    public RestResponse<String> batchDelete(String[] matterIds) {
        RestResponse<String> result =null;
        Subject subject = SecurityUtils.getSubject();
        String userId = (String) subject.getPrincipal();
        for (String mId : matterIds) {
            ProcessInfo pi = processInfoService.findByTaskId(mId);
            String processId = pi.getProcessId();
            int orderIndex = surviveProcessMapper.findOrderIndexByProcessId(processId);
            if(orderIndex>0){
                try{
                    List<MatterTask> mts = matterTaskMapper.findRightLineByProcessId(processId);
                    for(MatterTask mt:mts) {
                        String pIdSub = mt.getProcessId();
                        runtimeService.deleteProcessInstance(pIdSub, userId + "取消");
                        processService.updateSurviveProcess(ConstantUtil.PROCESS_DELETE_STATUS, pIdSub);
                        ProcessInfo subProcessInfo = mt.getProcessInfo();
                        subProcessInfo.setStatus(Status.DELETE.getValue());
                        processService.updateProcessInfoStatus(subProcessInfo);
                    }
                }catch (ActivitiObjectNotFoundException e){
                    logger.info("流程已取消：" + processId);
                }

            }else {
                try {
                    runtimeService.deleteProcessInstance(processId, userId + "取消");
                    processService.updateSurviveProcess(ConstantUtil.PROCESS_DELETE_STATUS, processId);
                    pi.setStatus(Status.DELETE.getValue());
                    processService.updateProcessInfoStatus(pi);
                } catch (ActivitiObjectNotFoundException e) {
                    logger.info("流程已取消：" + processId);
                }
            }
        }
        result = RestResponse.Success("处理成功");
        return result;
    }
}
