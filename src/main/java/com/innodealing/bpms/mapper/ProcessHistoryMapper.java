package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.History;
import com.innodealing.bpms.model.ProcessHistory;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessHistoryMapper {
    void insert(ProcessHistory processHistory);

    List<ProcessHistory> findByProcessId(String pId);
}
