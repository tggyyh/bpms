package com.innodealing.bpms.appconfig.mq;

import com.innodealing.bpms.model.EmailSendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailProducer {
    private final static Logger logger = LoggerFactory.getLogger(MailProducer.class);
    @Autowired
    private RabbitTemplate emailRabbitTemplate;
    @Value("${rabbitmq.mail.exchange}")
    private String EMAIL_EXCHANGE;
    @Value("${rabbitmq.mail.routingkey}")
    private String EMAIL_ROUTINGKEY;

    public void sendMq(EmailSendMessage mailRequest)throws Exception {
        emailRabbitTemplate.setExchange(EMAIL_EXCHANGE);
        emailRabbitTemplate.setRoutingKey(EMAIL_ROUTINGKEY);
        emailRabbitTemplate.convertAndSend(mailRequest);
        logger.info("邮件发送MQ bpms.mail"+mailRequest.toString());
    }

}