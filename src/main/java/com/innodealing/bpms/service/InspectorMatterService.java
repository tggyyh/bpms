package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.common.model.Status;
import com.innodealing.bpms.controller.CheckMatterController;
import com.innodealing.bpms.mapper.MatterTaskMapper;
import com.innodealing.bpms.model.Company;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.ProcessInfo;
import com.innodealing.commons.http.RestResponse;
import org.activiti.engine.*;
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

@Service("inspectorMatterService")
public class InspectorMatterService {
    private static final Logger logger = LoggerFactory.getLogger(InspectorMatterService.class);
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
    MatterTaskMapper matterTaskMapper;

    public Map<String,Object> findInspectorMatters(ReqData reqData) {
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
        List<Company> companies = matterTaskMapper.findInspectorCompany(reqData);
        for(Company company:companies){
            List<MatterTask> matterTasks = new ArrayList();
            reqData.put("companyName",company.getName());
            matterTasks = matterTaskMapper.findInspectorTasks(reqData);
            tasks.add(matterTasks);
        }
        page = new PageInfo(companies);
        Map<String, Object> map = new HashMap();
        map.put("total", page.getTotal());
        map.put("rows", tasks);
        return map;
    }

    public Map<String,Object> findOverMatters(ReqData reqData) {
        Integer pageNum = ConstantUtil.PAGE_NUM;
        Integer pageSize = ConstantUtil.PAGE_SIZE;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        PageInfo<MatterTask> page;
        PageHelper.offsetPage((pageNum-1)*pageSize, pageSize);
        List<MatterTask> matterTasks = new ArrayList();
        List<String> processList = new ArrayList();
        processList = matterTaskMapper.findInspectorOverTaskProcessIds(reqData);
        if(!CollectionUtils.isEmpty(processList)) {
            matterTasks = matterTaskMapper.findInspectorOverTasks(processList);
        }
        page = new PageInfo(processList);
        Map<String, Object> map = new HashMap();
        map.put("total", page.getTotal());
        map.put("rows", matterTasks);
        return map;
    }
}
