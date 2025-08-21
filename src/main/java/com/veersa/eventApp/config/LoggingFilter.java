package com.veersa.eventApp.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {

    private static final String REQUEST_ID = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // Generate a unique request ID
            String requestId = UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);

            // Optionally log the request path
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                String path = httpRequest.getRequestURI();
                String method = httpRequest.getMethod();
                System.out.println("[" + requestId + "] " + method + " " + path);
            }

            chain.doFilter(request, response);
        } finally {
            // Clean up MDC to avoid memory leaks
            MDC.clear();
        }
    }
}