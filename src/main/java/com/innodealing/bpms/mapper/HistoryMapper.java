package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Company;
import com.innodealing.bpms.model.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryMapper {
    int insertHistory(History history);

    List<History> findAll(ReqData reqData);
}
