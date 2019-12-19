package com.innodealing.bpms.service;

import com.google.gson.Gson;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.ProcessHistoryMapper;
import com.innodealing.bpms.mapper.ProcessInfoMapper;
import com.innodealing.bpms.model.ProcessHistory;
import com.innodealing.bpms.model.ProcessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("processInfoService")
public class ProcessInfoService {
    @Autowired
    ProcessInfoMapper processInfoMapper;
    @Autowired
    ProcessHistoryMapper processHistoryMapper;

    public List<ProcessInfo> findAll(ReqData reqData){
        return processInfoMapper.findAll(reqData);
    }

    public List<ProcessInfo> findByBond(ReqData reqData){
        return processInfoMapper.findByBond(reqData);
    }

    public List<ProcessInfo> findMailProcessInfo() {
        return processInfoMapper.findMailProcessInfo();
    }

    public List<ProcessInfo> findRightLIneMailProcessInfo() {
        return processInfoMapper.findRightLIneMailProcessInfo();
    }
    //查询项目未完成的流程
    public List<ProcessInfo> findByBondCode(String bondCode){
        return processInfoMapper.findByBondCode(bondCode);
    }

    public ProcessInfo findByTaskId(String taskId) {
        return processInfoMapper.findByTaskId(taskId);
    }

    public void insert(ProcessInfo pi) {
        processInfoMapper.insert(pi);
        ProcessHistory ph = new ProcessHistory();
        ph.setTaskType(0);
        ph.setProcessId(pi.getProcessId());
        Gson gson = new Gson();
        String jsonString = gson.toJson(pi);
        ph.setProcessInfo(jsonString);
        processHistoryMapper.insert(ph);
    }

    public void deleteByCompanyName(String companyName) {
        processInfoMapper.deleteByCompanyName(companyName);
    }
}
