package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.ProcessInfo;
import com.innodealing.bpms.model.SurviveProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SurviveProcessMapper {

    List<SurviveProcess> findCompanyProcess();
    List<SurviveProcess> findBondProcess();
    void insertSurviveProcess(List<SurviveProcess> list);
    void updateSurviveProcess(@Param("param0") int status, @Param("param1")String processId);

    List<SurviveProcess> findCustomBondProcess();

    List<SurviveProcess> findCustomCompanyProcess();

    List<SurviveProcess> findCustomRightLineProcess();

    void insert(SurviveProcess sp);

    List<SurviveProcess> findRightLineProcess();

    int findOrderIndexByProcessId(String processId);

    int findSameRemind(SurviveProcess sp);

//    SurviveProcess findById(Integer id);
//
//    void save(SurviveProcess surviveProcess);
//
//    void update(SurviveProcess surviveProcess);

    List<SurviveProcess> findByTemplateId(ReqData reqData);
}
