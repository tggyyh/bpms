package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.Upload;

public interface UploadMapper {
    Upload findById(int id);
    int insertUpload(Upload upload);
    int updateUpload(Upload upload);
    int deleteUpload(Upload upload);
    int auditUpload(Upload upload);
}
