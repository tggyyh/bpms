package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Company;
import com.innodealing.bpms.model.MatterTask;
import com.innodealing.bpms.model.ProcessInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MatterTaskMapper {
    MatterTask findByProcessId(String pId);
    List<MatterTask> findRightLineByProcessId(String pId);

    List<Company> findInspectorCompany(ReqData reqData);

    List<MatterTask> findInspectorTasks(ReqData reqData);

    List<Company> findManagerCompany(ReqData reqData);

    List<MatterTask> findManagerTasks(ReqData reqData);

    List<String> findManagerOverTaskProcessIds(ReqData reqData);

    List<MatterTask> findManagerOverTasks(List<String> list);

    List<MatterTask> findInspectorOverTasks(List<String> list);

    List<String> findInspectorOverTaskProcessIds(ReqData reqData);

    List<Company> findConfirmCompany(ReqData reqData);
    List<MatterTask> findConfirmTasks(ReqData reqData);

    List<Company> findCheckCompany(ReqData reqData);
    List<MatterTask> findCheckTasks(ReqData reqData);

    List<Company> findInspectorTraceCompany(ReqData reqData);
    List<MatterTask> findInspectorTraceTasks(ReqData reqData);

    List<Company> findHandleCompany(ReqData reqData);
    List<MatterTask> findHandleTasks(ReqData reqData);

    List<Company> findManagerTraceCompany(ReqData reqData);
    List<MatterTask> findManagerTraceTasks(ReqData reqData);

}
