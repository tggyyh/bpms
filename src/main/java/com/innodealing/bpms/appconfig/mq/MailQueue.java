package com.innodealing.bpms.appconfig.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailQueue {
	@Value("${rabbitmq.mail.exchange}")
	private String MAIL_EXCHANGE;

	@Value("${rabbitmq.mail.routingkey}")
	private String MAIL_ROUTINGKEY;

	@Value("${rabbitmq.mail.queue}")
	private String MAIL_QUEUE;

	@Bean
	public Exchange mailExchange() {
		return new DirectExchange(MAIL_EXCHANGE, false, false);
	}

	@Bean
	public Queue mailQueueMQ() {
		return new Queue(MAIL_QUEUE, true, false, false);
	}

	@Bean
	public Binding mailBs() {
		return new Binding(MAIL_QUEUE, Binding.DestinationType.QUEUE, MAIL_EXCHANGE, MAIL_ROUTINGKEY, null);
	}
}