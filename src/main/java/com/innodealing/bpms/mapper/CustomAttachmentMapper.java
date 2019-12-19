package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.CustomAttachment;

import java.util.List;

public interface CustomAttachmentMapper {
    int insertCustomAttachment(CustomAttachment customAttachment);
    int insertCustomAttachmentList(List<CustomAttachment> customAttachmentList);
    int deleteById(int id);
    int deleteByCustomId(int customId);
}
