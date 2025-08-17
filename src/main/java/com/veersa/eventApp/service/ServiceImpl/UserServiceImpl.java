package com.veersa.eventApp.service.ServiceImpl;
import com.veersa.eventApp.DTO.UserResponse;
import com.veersa.eventApp.exception.UserNotFoundException;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.UserService;
import com.veersa.eventApp.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public UserResponse getUserById() {

        User user = securityUtils.getCurrentUser();
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        return response;
    }
}
