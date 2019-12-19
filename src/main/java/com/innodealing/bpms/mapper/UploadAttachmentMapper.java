package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.UploadAttachment;

import java.util.List;

public interface UploadAttachmentMapper {
    int insertUploadAttachmentList(List<UploadAttachment> uploadAttachmentList);
    int deleteUploadAttachmentByUploadId(int uploadId);
    int deleteUploadAttachmentById(int id);
}
