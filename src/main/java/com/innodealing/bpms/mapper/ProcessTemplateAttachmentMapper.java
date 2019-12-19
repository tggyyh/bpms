package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.ProcessTemplateAttachment;
import com.innodealing.bpms.model.SurviveProcess;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcessTemplateAttachmentMapper {
    void insertProcessTemplateAttachment(List<ProcessTemplateAttachment> attachmentList);

    void deleteProcessTemplateAttachment(String processId);
}
