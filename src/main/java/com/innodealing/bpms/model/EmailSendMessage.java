package com.innodealing.bpms.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class EmailSendMessage implements Serializable{
    private static final long serialVersionUID = -4143529791604151797L;
    private String[] to;
    private String[] toSecret;
    private String subject;
    private String text;
    private List<? extends Attachment> attachmentList;

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<? extends Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<? extends Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String[] getToSecret() {
        return toSecret;
    }

    public void setToSecret(String[] toSecret) {
        this.toSecret = toSecret;
    }

    @Override
    public String toString() {
        return "EmailSendMessage{" +
                "to=" + Arrays.toString(to) +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
