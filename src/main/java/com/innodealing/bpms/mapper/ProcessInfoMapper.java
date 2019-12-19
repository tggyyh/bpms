package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.ProcessInfo;
import com.innodealing.bpms.model.SurviveProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcessInfoMapper {
    void insertProcessInfo(List<ProcessInfo> processInfoList);

    void updateProcessInfo(ProcessInfo processInfo);

    void updateProcessInfoStatus(ProcessInfo processInfo);

    ProcessInfo findByProcessId(String processId);

    List<ProcessInfo> findAll(ReqData reqData);

    void updateHandleProcessInfo(ProcessInfo processInfo);

    List<ProcessInfo> findByBond(ReqData reqData);

    void updateNoticeFlag(@Param("0")String processId, @Param("1") int flag);

    void insert(ProcessInfo pi);

    List<ProcessInfo> findMailProcessInfo();

    List<ProcessInfo> findRightLIneMailProcessInfo();

    void updateCheckProcessInfo(ProcessInfo processInfo);

    List<ProcessInfo> findByBondCode(String bondCode);

    ProcessInfo findByTaskId(String taskId);

    void deleteByCompanyName(String companyName);
}
