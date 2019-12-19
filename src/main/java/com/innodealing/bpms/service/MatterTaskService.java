package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.MatterTaskMapper;
import com.innodealing.bpms.mapper.ModuleMapper;
import com.innodealing.bpms.model.MatterTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("matterTaskService")
public class MatterTaskService {
    @Autowired
    MatterTaskMapper matterTaskMapper;
    public MatterTask findByProcessId(String pId) {
        return matterTaskMapper.findByProcessId(pId);
    }

    public List<MatterTask> findRightLineByProcessId(String pId) {
        return matterTaskMapper.findRightLineByProcessId(pId);
    }
}
