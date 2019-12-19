package com.innodealing.bpms.task;

import com.innodealing.bpms.appconfig.mq.MailProducer;
import com.innodealing.bpms.mail.MailService;
import com.innodealing.bpms.model.EmailSendMessage;
import com.innodealing.bpms.model.ProcessInfo;
import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.HolidayService;
import com.innodealing.bpms.service.ProcessInfoService;
import com.innodealing.bpms.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//非行权付息 超过提醒频率仍未处理事项
@Component
public class MailTimerTask extends MatterTask{
    @Autowired
    private HolidayService holidayService;
    @Autowired
    private ProcessInfoService processInfoService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private MailService mailService;
    @Autowired
    private MailProducer mailProducer;

    private static final Logger log = LoggerFactory.getLogger(MailTimerTask.class);
    private static final String EMAIL_MANAGER="email_manager";

    @Scheduled(cron="0 5 9 * * *")
//    @Scheduled(fixedDelay=5000000)
    public void handle() {
        super.handle(EMAIL_MANAGER);
    }
    public void execute() {
        Long beginTime = System.currentTimeMillis();
        try {
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(d);
            Date date2 = sdf.parse(dateStr);
            List<ProcessInfo> list = processInfoService.findMailProcessInfo();
            for(ProcessInfo pi: list){
                int mailFrequency =  pi.getMailFrequency()+1;
                Date completeDate = pi.getCompleteDate();
                int beforeDay = pi.getMailBeforeDay();
                Date date = holidayService.calculateDate(completeDate,beforeDay);
                int day = holidayService.workDay(date, date2);
                if(day==0 ||(day>0 && mailFrequency!=0 && 0 == day%mailFrequency )){
                    sendEmail(pi);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        log.info("耗时:" + (System.currentTimeMillis()-beginTime));
    }
    private void sendEmail(ProcessInfo pi) {
        List<User> users = processService.getManagerUser(pi);
        Set<String> to = new HashSet();
        users.stream().filter(s -> !StringUtils.isEmpty(s.getEmail()) && s.getEmail().contains("@"))
                .forEach(s -> to.add(s.getEmail()));
        if (to.size() > 0) {
            EmailSendMessage message = new EmailSendMessage();
            message.setTo(to.toArray(new String[to.size()]));
            int type = pi.getType();
            StringBuffer sb = new StringBuffer();

            if(0 == type){
                sb.append(pi.getCompanyName()).append("—");
            }else{
                sb.append(pi.getBondShortname()).append("—");
            }
            sb.append(pi.getName());
            sb.append("(");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月");
            sb.append(sf.format(pi.getRemindDate())).append(")");
            message.setSubject(sb.toString());
            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
            String text= pi.getContent();
            message.setText(text);
            message.setAttachmentList(pi.getProcessTemplateAttachmentList());
            try {
//                MailThreadPool.getPool().submit(new MailTask(message, mailService));
                mailProducer.sendMq(message);
            } catch (Exception e) {
                log.error("邮件发送错误,processId:" + pi.getProcessId(), e);
            }
        }
    }

}
