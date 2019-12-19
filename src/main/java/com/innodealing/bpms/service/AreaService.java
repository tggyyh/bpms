package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.AreaMapper;
import com.innodealing.bpms.model.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("areaService")
public class AreaService {
    @Autowired
    private AreaMapper areaMapper;

    public List<Area> findAll(){
        return areaMapper.findAll();
    }

    public List<Area> findAllParent(){
        return areaMapper.findAllParent();
    }
}
