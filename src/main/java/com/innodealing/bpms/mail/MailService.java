package com.innodealing.bpms.mail;

import com.innodealing.bpms.appconfig.ftpconfig.FtpClientFactory;
import com.innodealing.bpms.model.Attachment;
import com.innodealing.bpms.model.EmailSendMessage;
import com.innodealing.bpms.unit.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.List;

@Component
public class MailService {
    static {
        System.setProperty("mail.mime.splitlongparameters","false");
    }
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private FtpClientFactory ftpClientFactory;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.mail.secret}")
    private String secret;
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    public void sendMessage(EmailSendMessage emailSendMessage) {
//        logger.info(MailThreadPool.getPool().toString());
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        FTPClient ftp = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(emailSendMessage.getTo());
            helper.setBcc(secret);
            helper.setSubject(emailSendMessage.getSubject());
            helper.setText(emailSendMessage.getText(),true);
            ftp = ftpClientFactory.getClient();
            List<? extends Attachment> attachmentList = emailSendMessage.getAttachmentList();
            if (!CollectionUtils.isEmpty(attachmentList)){
                for (Attachment attachment : attachmentList) {
                    String alias = attachment.getUrl().substring(attachment.getUrl().lastIndexOf("/") + 1);
//                  helper.addAttachment(attachment.getName(), new UrlResource(attachment.getUrl()));
                    ftp.enterLocalPassiveMode();
                    InputStream in = ftp.retrieveFileStream(alias);
                    helper.addAttachment(attachment.getName(),new ByteArrayResource(IOUtils.toByteArray(in)));
                    in.close();
                    ftp.completePendingCommand();
                }
            }
            FtpUtil.close(ftp);
            Long beginTime1 = System.currentTimeMillis();
            emailSender.send(message);
            logger.info("mail to server spend time"+(System.currentTimeMillis()-beginTime1));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }finally {
            if(ftp!=null){
                FtpUtil.close(ftp);
            }
        }
//        logger.info(MailThreadPool.getPool().toString());
    }

}
