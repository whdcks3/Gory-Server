package com.whdcks3.common;

import java.util.Date;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EmailUtils {
    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            simpleMailMessage.setSentDate(new Date());

            javaMailSender.send(simpleMailMessage);

            log.info("Success");
        } catch (Exception e) {
            log.info("Fail");
            e.printStackTrace();
        }
    }
}
// cfal ribj yaqt yhoa