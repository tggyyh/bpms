package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.IndustryMapper;
import com.innodealing.bpms.model.Industry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("industryService")
public class IndustryService {

    @Autowired
    IndustryMapper industryMapper;

    public List<Industry> findAll(){
        return industryMapper.findAll();
    }

    public List<Industry> findAllParent(){
        return industryMapper.findAllParent();
    }
}
