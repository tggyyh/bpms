package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.DocumentFile;

import java.util.List;

public interface DocumentFileMapper {
    List<DocumentFile> findCompanyAll(ReqData reqData);
    List<DocumentFile> findBondAll(ReqData reqData);
    List<DocumentFile> findCompanyProcessAll(ReqData reqData);
    List<DocumentFile> findBondProcessAll(ReqData reqData);
}
