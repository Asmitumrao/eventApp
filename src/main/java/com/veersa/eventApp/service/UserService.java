package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public UserResponse getUserById(Long id);
}
