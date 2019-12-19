package com.innodealing.bpms.service;


import com.innodealing.bpms.mapper.SynDeptMapper;
import com.innodealing.bpms.model.SynDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("synDeptService")
public class SynDeptService {
    @Autowired
    SynDeptMapper synDeptMapper;

    public void save(List<SynDept> list)throws Exception  {
        synDeptMapper.delete();
        synDeptMapper.save(list);
    }
}
