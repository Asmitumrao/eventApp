package com.veersa.eventApp.feign;


import com.veersa.eventApp.DTO.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "emailer-service", url = "http://localhost:8081" ,fallback = EmailerFallback.class, configuration = EmailerFeignConfig.class)
public interface EmailerClient {

    @PostMapping("/api/email/send")
    void sendEmail(@RequestBody EmailRequest request);
}
