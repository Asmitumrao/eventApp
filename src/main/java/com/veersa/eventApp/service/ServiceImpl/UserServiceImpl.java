package com.veersa.eventApp.service.ServiceImpl;
import com.veersa.eventApp.DTO.UserResponse;
import com.veersa.eventApp.exception.UserNotFoundException;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getName());

        return response;
    }
}
