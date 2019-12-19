package com.innodealing.bpms.service;

import com.innodealing.bpms.mapper.UploadAttachmentMapper;
import com.innodealing.bpms.mapper.UploadMapper;
import com.innodealing.bpms.model.Upload;
import com.innodealing.bpms.model.UploadAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("uploadService")
public class UploadService {

    @Autowired
    UploadMapper uploadMapper;
    @Autowired
    UploadAttachmentMapper uploadAttachmentMapper;

    public Upload findById(int id){
        Upload upload = uploadMapper.findById(id);
        return upload;
    }

    public int insertUpload(Upload upload){
        int result = uploadMapper.insertUpload(upload);
        if(upload.getId()>0 && null!=upload.getUploadAttachmentList() && upload.getUploadAttachmentList().size()>0){
            for(UploadAttachment uploadAttachment : upload.getUploadAttachmentList()){
                uploadAttachment.setUploadId(upload.getId());
            }
            uploadAttachmentMapper.insertUploadAttachmentList(upload.getUploadAttachmentList());
        }
        return result;
    }

    public int uploadUpload(Upload upload){
        int result = uploadMapper.updateUpload(upload);
        result = uploadAttachmentMapper.deleteUploadAttachmentByUploadId(upload.getId());
        if(null!=upload.getUploadAttachmentList() && upload.getUploadAttachmentList().size()>0) {
            result = uploadAttachmentMapper.insertUploadAttachmentList(upload.getUploadAttachmentList());
        }
        return result;
    }

    public int deleteUpload(Upload upload){
        int result = uploadAttachmentMapper.deleteUploadAttachmentByUploadId(upload.getId());
        result = uploadMapper.deleteUpload(upload);
        return result;
    }

    public int deleteUploadAttachmentById(int id){
        int result = uploadAttachmentMapper.deleteUploadAttachmentById(id);
        return result;
    }

    public int auditUpload(Upload upload){
        int result = uploadMapper.auditUpload(upload);
        return result;
    }
}
