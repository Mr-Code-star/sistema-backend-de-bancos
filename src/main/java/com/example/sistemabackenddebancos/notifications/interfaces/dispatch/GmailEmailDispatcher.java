package com.example.sistemabackenddebancos.notifications.interfaces.dispatch;

import com.example.sistemabackenddebancos.notifications.interfaces.dispatch.email.EmailDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class GmailEmailDispatcher implements EmailDispatcher {

    private final JavaMailSender mailSender;
    private final String from;

    public GmailEmailDispatcher(JavaMailSender mailSender,
                                @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String toEmail, String subject, String body) {
        var msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}