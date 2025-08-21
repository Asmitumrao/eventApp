package com.veersa.eventApp.util;

import com.veersa.eventApp.exception.UserNotFoundException;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    private UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public String getCurrentUserEmail() {
        UserDetails userDetails = getUserDetails();
        if (userDetails != null) {
            return userDetails.getUsername();
        }
        throw new UserNotFoundException("User not authenticated");
    }

    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }


}
