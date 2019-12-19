package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.ProcessAttachment;
import com.innodealing.bpms.model.ProcessInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcessAttachmentMapper {
    void insertProcessAttachment(List<ProcessAttachment> processAttachmentList);

    void deleteProcessAttachment(String processId);
}
