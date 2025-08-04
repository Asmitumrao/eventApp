package com.veersa.eventApp.util;

import com.veersa.eventApp.exception.UserNotFoundException;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class SecurityUtils {


    private final UserDetails userDetails;
    public  final UserRepository userRepository;// Assuming UserRepository is a singleton or has a static getInstance method

    @Autowired
    public SecurityUtils(UserRepository userRepository) {

        this.userRepository = userRepository;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (UserDetails) authentication.getPrincipal();
        }
        else {
            userDetails = null; // or throw an exception if preferred
        }
    }


    public String getCurrentUserEmail() {
        if (userDetails != null) {
            return userDetails.getUsername();
        }
        return null; // or throw an exception if preferred
    }

    public User getCurrentUser(){

        if(userDetails!=null){
            String email = userDetails.getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        }else {
            return null;
        }
    }
    public Long getCurrentUserId() {
        User user = getCurrentUser();
        if (user != null) {
            return user.getId();
        }
        throw new UserNotFoundException("User not found");
    }
}
