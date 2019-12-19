package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.Industry;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndustryMapper {
    List<Industry> findAll();
    List<Industry> findAllParent();
}
