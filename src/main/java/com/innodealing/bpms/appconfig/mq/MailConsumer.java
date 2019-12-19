package com.innodealing.bpms.appconfig.mq;

import com.innodealing.bpms.mail.MailService;
import com.innodealing.bpms.model.EmailSendMessage;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MailConsumer {
    @Autowired
    private MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(MailConsumer.class);
    @RabbitListener(queues = "${rabbitmq.mail.queue}")
    public void receiveRequest(EmailSendMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            logger.info("receive Queue Message:" + message.toString());
            mailService.sendMessage(message);
            channel.basicAck(tag, false);
            //因为天风邮箱服务器限制用户每分钟最多发50封邮件。单线程睡2.5s，单服务器最多每分钟发60/2.5=24封。
            Thread.sleep(2500);
        } catch (Exception ex) {
            channel.basicNack(tag, false,false);
            logger.error(ex.getMessage(), ex);
        }
    }
}
