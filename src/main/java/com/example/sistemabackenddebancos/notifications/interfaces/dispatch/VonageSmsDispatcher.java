package com.example.sistemabackenddebancos.notifications.interfaces.dispatch;

import com.example.sistemabackenddebancos.notifications.interfaces.dispatch.sms.SmsDispatcher;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.SmsClient;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VonageSmsDispatcher implements SmsDispatcher {

    private final SmsClient smsClient;
    private final String from;

    public VonageSmsDispatcher(@Value("${vonage.api.key}") String key,
                               @Value("${vonage.api.secret}") String secret,
                               @Value("${vonage.sms.from:BankApp}") String from) {
        this.smsClient = VonageClient.builder().apiKey(key).apiSecret(secret).build().getSmsClient();
        this.from = from;
    }

    @Override
    public void send(String toPhoneE164, String message) {
        smsClient.submitMessage(new TextMessage(from, toPhoneE164, message));
    }
}