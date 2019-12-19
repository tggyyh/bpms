package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.EconomicDepartment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EconomicDepartmentMapper {
    List<EconomicDepartment> findAll();
    List<EconomicDepartment> findAllParent();
}
