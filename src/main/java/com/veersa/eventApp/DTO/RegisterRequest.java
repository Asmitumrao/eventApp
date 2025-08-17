package com.veersa.eventApp.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(min=6,max=20,message = "Password must be at least 6 characters long and at most 20 characters long")
    private String password;


    @NotBlank(message = "Role is required")
    private String role; // Optional: or default it in service layer
}
