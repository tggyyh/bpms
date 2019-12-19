package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.EconomicDepartmentMapper;
import com.innodealing.bpms.model.EconomicDepartment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("economicDepartmentService")
public class EconomicDepartmentService {
    @Autowired
    EconomicDepartmentMapper economicDepartmentMapper;

    public List<EconomicDepartment> findAll(){
        return economicDepartmentMapper.findAll();
    }

    public List<EconomicDepartment> findAllParent(){
        return economicDepartmentMapper.findAllParent();
    }
}
