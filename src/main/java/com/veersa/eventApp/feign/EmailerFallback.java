package com.veersa.eventApp.feign;

import com.veersa.eventApp.DTO.EmailRequest;
import org.springframework.stereotype.Component;

@Component
public class EmailerFallback implements  EmailerClient {

    @Override
    public void sendEmail(EmailRequest request) {
        System.out.println("⚠️ Emailer service is down, could not send: " + request);
    }
}
