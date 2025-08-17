package com.veersa.eventApp.controller;


import com.veersa.eventApp.DTO.UserResponse;
import com.veersa.eventApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {


    @Autowired
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserResponse> getUserById() {
        UserResponse userResponse = userService.getUserById();
        return ResponseEntity.ok(userResponse);
    }
}
