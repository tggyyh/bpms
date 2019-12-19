package com.innodealing.bpms.task;

import com.innodealing.bpms.mail.MailService;
import com.innodealing.bpms.model.EmailSendMessage;

import java.util.concurrent.Callable;

public class MailTask implements Callable {
    private MailService mailService;
    private EmailSendMessage emailSendMessage;
    public MailTask(EmailSendMessage emailSendMessage,MailService mailService){
        this.emailSendMessage = emailSendMessage;
        this.mailService = mailService;
    }
    @Override
    public Object call() throws Exception {
        mailService.sendMessage(emailSendMessage);
        return null;
    }
}
