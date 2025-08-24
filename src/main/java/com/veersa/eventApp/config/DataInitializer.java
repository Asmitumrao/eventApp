package com.veersa.eventApp.config;

import com.veersa.eventApp.model.Role;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.RoleRepository;
import com.veersa.eventApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.email}")
    private String superAdminEmail;

    @Value("${app.superadmin.password}")
    private String superAdminPassword;

    @Override
    public void run(String... args) {
        // Ensure roles exist
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
        if (roleRepository.findByName("ROLE_ORGANIZER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ORGANIZER"));
        }

        // Create super admin if not exists
        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            User superAdmin = new User();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword)); // 🔐 secure password
            superAdmin.setUsername("asmit");

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").get();
            superAdmin.setRole(adminRole);

            userRepository.save(superAdmin);
            log.info("✅ Super Admin created with email: " + superAdminEmail);
        } else {
            log.info("ℹ️ Super Admin already exists with email: " + superAdminEmail);
        }
    }
}
