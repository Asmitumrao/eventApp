package com.veersa.eventApp.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EmailerFeignConfig {
    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-API-KEY",
                    System.getenv("EMAILER_API_KEY")); // 🔑 keep in env, not hardcoded
        };
    }
}
