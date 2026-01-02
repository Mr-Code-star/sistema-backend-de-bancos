package com.example.sistemabackenddebancos.notifications.interfaces.dispatch.email;

public interface EmailDispatcher {
    void send(String toEmail, String subject, String body);
}