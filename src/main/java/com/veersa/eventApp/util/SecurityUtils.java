package com.veersa.eventApp.util;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class SecurityUtils {


    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }

        return null;
    }

    public Long getCurrentUserId() {
        UserDetails userDetails = getCurrentUser();
        if (userDetails != null) {
            return ((com.veersa.eventApp.model.User) userDetails).getId();
        }
        return null; // or throw an exception if preferred
    }
}
