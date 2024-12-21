package com.whdcks3.portfolio.gory_server.common;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class EmailUtils {
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String subject = "[고리] 회원가입 이메일 인증";
        String verificationLink = "http://localhost:8080/api/auth/activate?token=" + token;
        String content = "<p>회원가입을 완료하려면 아래 링크를 클릭하여 이메일을 인증해주세요.</p>"
                + "<a href=\"" + verificationLink + "\">이메일 인증 링크</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.info("Fail");
            e.printStackTrace();
        }
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            simpleMailMessage.setSentDate(new Date());

            mailSender.send(simpleMailMessage);

            log.info("Success");
        } catch (Exception e) {
            log.info("Fail");
            e.printStackTrace();
        }
    }
}
// cfal ribj yaqt yhoa