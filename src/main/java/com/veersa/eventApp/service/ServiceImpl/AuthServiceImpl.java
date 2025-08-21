package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.DTO.AuthRequest;
import com.veersa.eventApp.DTO.AuthResponse;
import com.veersa.eventApp.DTO.ChangePasswordRequest;
import com.veersa.eventApp.DTO.RegisterRequest;
import com.veersa.eventApp.exception.*;
import com.veersa.eventApp.model.Role;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.RoleRepository;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.security.JwtUtil;
import com.veersa.eventApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final  PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Override
    public AuthResponse register(RegisterRequest request) {

        // check if user already exists
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new UserAlreadyPresentException("User with email " + request.getEmail() + " already exists");
        }

        // Find role from DB
        Role role = roleRepository.findByName("ROLE_" + request.getRole().toUpperCase())
                .orElseThrow(() -> new UserRoleException("Role not found: " + request.getRole()));

        //check if role is Not Admin
        if (role.getName().equals("ROLE_ADMIN")) {
            throw new UserRoleException("Invalid role: " + request.getRole() + ". Only ORGANIZER or USER roles are allowed.");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        // Save user to the database
        userRepository.save(user);


        // call login method to generate token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(request.getEmail());
        authRequest.setPassword(request.getPassword());

        // Return the login response
        return  login(authRequest);

    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            AuthResponse authResponse = new AuthResponse(
                    token,
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole().getName(),
                    user.getId()
            );
            return authResponse;

        } catch (BadCredentialsException e) {
            throw new IncorrectPasswordException("Invalid email or password");
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void changePassword(ChangePasswordRequest request) {

        // fetch user from UserDetails
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


}
