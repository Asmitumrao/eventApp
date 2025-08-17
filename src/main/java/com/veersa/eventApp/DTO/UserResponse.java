package com.veersa.eventApp.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserResponse {


    private Long id;
    private String username;
    private String email;
    private String role;
}
