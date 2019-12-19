package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.SurviveProcess;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExceptionProcessMapper {
    void insert(SurviveProcess exceptionProcess);
}
