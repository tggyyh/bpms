package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.controller.CheckMatterController;
import com.innodealing.bpms.mapper.MatterTaskMapper;
import com.innodealing.bpms.model.Company;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.ProcessAttachment;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("handleMatterService")
public class HandleMatterService {
    private static final Logger logger = LoggerFactory.getLogger(HandleMatterService.class);

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
    private RepositoryService repositoryService;
    @Autowired
    private ProcessService processService;

    @Autowired
    MatterTaskMapper matterTaskMapper;

    public RestResponse<String> handlePass(MatterTask mt) {
        RestResponse<String> result =null;
        try {
            Subject subject = SecurityUtils.getSubject();
            String userId = (String) subject.getPrincipal();
            String taskId = mt.getId();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != task) {
                String exId = task.getExecutionId();
                taskService.setAssignee(taskId, userId);
                taskService.complete(taskId);
                processService.updateData1(mt);
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

    public Map<String,Object> findHandleMatters(ReqData reqData) {
        Subject subject = SecurityUtils.getSubject();
        String userId = (String) subject.getPrincipal();
        reqData.put("userId",userId);
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
        List<Company> companies = matterTaskMapper.findHandleCompany(reqData);
        for(Company company:companies){
            List<MatterTask> matterTasks = new ArrayList();
            reqData.put("companyName",company.getName());
            matterTasks = matterTaskMapper.findHandleTasks(reqData);
            tasks.add(matterTasks);
        }
        page = new PageInfo(companies);
        Map<String, Object> map = new HashMap();
        map.put("total", page.getTotal());
        map.put("rows", tasks);
        return map;
    }

    public RestResponse<String> updateNoticeFlag(String processId,int flag) {
        RestResponse<String> result =null;
        try {
            processService.updateNoticeFlag(processId,flag);
            result = RestResponse.Success("修改成功");
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            result = RestResponse.Fail("修改失败");
        }
        return  result;
    }
}
