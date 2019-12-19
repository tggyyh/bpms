package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.ProcessHistoryMapper;
import com.innodealing.bpms.model.ProcessHistory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("processHistoryService")
public class ProcessHistoryService {
    @Autowired
    private ProcessHistoryMapper processHistoryMapper;

    public void insert(ProcessHistory processHistory){
        processHistoryMapper.insert(processHistory);
    }

    public List<ProcessHistory> findByProcessId(String pId) {
        return processHistoryMapper.findByProcessId(pId);
    }
}
