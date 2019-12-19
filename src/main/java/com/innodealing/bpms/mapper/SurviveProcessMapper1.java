package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.SurviveProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SurviveProcessMapper1 {

    int findSameRemind1(SurviveProcess sp);

}
