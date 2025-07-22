package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.AuthRequest;
import com.veersa.eventApp.DTO.ChangePasswordRequest;
import com.veersa.eventApp.DTO.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {

    ResponseEntity<?> register(RegisterRequest request);

    ResponseEntity<?> login(AuthRequest request);

    //ResponseEntity<?> updateUser(Long id, RegisterRequest request);

    void changePassword(ChangePasswordRequest request);
}