package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.SynDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SynDeptMapper {
    void delete();
    void save(List<SynDept> list);
}
