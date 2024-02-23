package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	private static Logger logger = LoggerFactory.getLogger(EmailService.class);

	public boolean sendEmail(String to, String subject, String text) {

	    String from = "testjavamailspringboot@gmail.com"; // Replace with your Gmail address
		
		boolean flag = false;
		try {
			SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom(from); // Set the "from" address
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);

			flag = true;
		} catch (Exception e) {
			logger.error("Exception :- {}", e);
		}
		return flag;
	}
}
