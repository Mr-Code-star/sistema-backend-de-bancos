package com.example.sistemabackenddebancos.notifications.interfaces.dispatch.sms;

public interface SmsDispatcher {
    void send(String toPhoneE164, String message);
}