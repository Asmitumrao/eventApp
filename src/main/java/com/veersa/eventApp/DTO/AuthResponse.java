package com.veersa.eventApp.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthResponse {

    private String token;
    private String email;
    private String username;
    private String role;
    private Long userId;

    public AuthResponse(String token, String email, String username, String role, Long userId) {
        this.token = token;
        this.email = email;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }
    @Override
    public String toString() {
        return "AuthResponse(token=PROTECTED)";
    }
}
