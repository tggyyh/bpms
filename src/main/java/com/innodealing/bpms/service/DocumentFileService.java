package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.DocumentFileMapper;
import com.innodealing.bpms.model.DocumentAttachment;
import com.innodealing.bpms.model.DocumentFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service("documentFileService")
public class DocumentFileService {
    @Autowired
    DocumentFileMapper documentFileMapper;

    public PageInfo<DocumentFile> findCompanyAllByPage(ReqData reqData){

        Integer pageNum = 0;
        Integer pageSize = 30;
        PageInfo page;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        try {
            PageHelper.offsetPage(pageNum*pageSize, pageSize);
            List<DocumentFile> documentFileList = documentFileMapper.findCompanyProcessAll(reqData);
            List<DocumentFile> fileList = documentFileMapper.findCompanyAll(reqData);
            documentFileList.stream().forEach(documentFile ->{
                        List<DocumentFile> df = new ArrayList<DocumentFile>();
                        if(documentFile.getOrderIndex()>0){
                            //行权付息
                            df = fileList.stream().filter(attachment ->
                                    documentFile.getIsFiles() == attachment.getIsFiles() && documentFile.getCustomId()==attachment.getCustomId() && documentFile.getCustomCreateTime().getTime()==attachment.getCustomCreateTime().getTime()
                            ).collect(Collectors.toList());
                        }else{
                            df = fileList.stream().filter(attachment ->
                                    documentFile.getId() == attachment.getId() && documentFile.getIsFiles() == attachment.getIsFiles()
                            ).collect(Collectors.toList());
                        }
                        if(null!=df && df.size()>0){
                            if(df.get(0)!=null && null != df.get(0).getDocumentAttachmentList() && df.get(0).getDocumentAttachmentList().size()>0){
                                for(DocumentFile dfFile : df){
                                    documentFile.getDocumentAttachmentList().addAll(dfFile.getDocumentAttachmentList());
                                }
                                documentFile.setUpdateTime(df.stream().max((a,b)->a.getUpdateTime().compareTo(b.getUpdateTime())).get().getUpdateTime());
                            }
                        }
                    });

            page = new PageInfo(documentFileList.stream().filter(x->x.getDocumentAttachmentList().size()!=0).collect(Collectors.toList()));

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public PageInfo<DocumentFile> findBondAllByPage(ReqData reqData){

        Integer pageNum = 0;
        Integer pageSize = 30;
        PageInfo page;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        try {
            PageHelper.offsetPage(pageNum*pageSize, pageSize);
            List<DocumentFile> documentFileList = documentFileMapper.findBondProcessAll(reqData);
            List<DocumentFile> fileList = documentFileMapper.findBondAll(reqData);
            documentFileList.stream().forEach(documentFile ->{
                List<DocumentFile> df = new ArrayList<DocumentFile>();
                if(documentFile.getOrderIndex()>0){
                    //行权付息
                    df = fileList.stream().filter(attachment ->
                            documentFile.getIsFiles() == attachment.getIsFiles() && documentFile.getCustomId()==attachment.getCustomId() && documentFile.getCustomCreateTime().getTime()==attachment.getCustomCreateTime().getTime()
                    ).collect(Collectors.toList());
                }else{
                    df = fileList.stream().filter(attachment ->
                            documentFile.getId() == attachment.getId() && documentFile.getIsFiles() == attachment.getIsFiles()
                    ).collect(Collectors.toList());
                }
                if(null!=df && df.size()>0){
                    if(df.get(0)!=null && null != df.get(0).getDocumentAttachmentList() && df.get(0).getDocumentAttachmentList().size()>0){
                        for(DocumentFile dfFile : df){
                            documentFile.getDocumentAttachmentList().addAll(dfFile.getDocumentAttachmentList());
                        }
                        documentFile.setUpdateTime(df.stream().max((a,b)->a.getUpdateTime().compareTo(b.getUpdateTime())).get().getUpdateTime());
                    }
                }
            });
            page =new PageInfo(documentFileList.stream().filter(x->x.getDocumentAttachmentList().size()!=0).collect(Collectors.toList()));

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }
}
