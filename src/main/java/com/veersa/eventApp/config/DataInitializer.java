package com.veersa.eventApp.config;

import com.veersa.eventApp.model.Role;
import com.veersa.eventApp.respository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner{

    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
        if (roleRepository.findByName("ROLE_ORGANIZER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ORGANIZER"));
        }
    }
}
