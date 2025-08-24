package com.veersa.eventApp.feign;


import com.veersa.eventApp.DTO.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "emailer-service", url = "${notification.service.url}" ,fallback = EmailerFallback.class)
public interface EmailerClient {

    @PostMapping("/api/email/send")
    void sendEmail(@RequestBody EmailRequest request);
}
