package com.example.financetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String appEmail;


    public void sendEmail(String subject, String to, String text, boolean hasHtml, File file, String fileName){
        new Thread(() -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setSubject(subject);
                helper.setFrom(appEmail, "Finance Tracker Support");
                helper.setTo(to);
                helper.setReplyTo(appEmail);
                helper.setText(text, hasHtml);
                if (file != null) {
                    helper.addAttachment(fileName, file);
                }
                javaMailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
