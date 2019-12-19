package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.TemplateAttachment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TemplateAttachmentMapper {
    List<TemplateAttachment> findAll();
    TemplateAttachment findById(int id);
    List<TemplateAttachment> findByTemplateId(int templateId);
    int insertTemplateAttachment(TemplateAttachment templateAttachment);
    int insertTemplateAttachmentList(List<TemplateAttachment> templateAttachmentList);
    int updateTemplateAttachment(TemplateAttachment templateAttachment);
    int deleteById(int id);
    int deleteByTemplateId(int templateId);
}
