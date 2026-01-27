package com.example.sistemabackenddebancos.iam.application.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JavaMailEmailService {

    private final JavaMailSender sender;
    private final String from;

    public JavaMailEmailService(JavaMailSender sender, @Value("${spring.mail.username}") String from) {
        this.sender = sender;
        this.from = from;
    }

    public void send(String to, String subject, String body) {
        var msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        sender.send(msg);
    }
}