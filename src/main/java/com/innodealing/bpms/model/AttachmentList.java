package com.innodealing.bpms.model;

import java.util.List;

public class AttachmentList {
    private List<Attachment> attachments;

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    public AttachmentList(){super();}
    public AttachmentList(List<Attachment> attachments){
        super();
        this.attachments=attachments;
    }
}
