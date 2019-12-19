package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.Area;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AreaMapper {
    List<Area> findAll();
    List<Area> findAllParent();
}
