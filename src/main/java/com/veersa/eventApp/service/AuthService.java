package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.AuthRequest;
import com.veersa.eventApp.DTO.AuthResponse;
import com.veersa.eventApp.DTO.ChangePasswordRequest;
import com.veersa.eventApp.DTO.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(AuthRequest request);

    void changePassword(ChangePasswordRequest request);
}