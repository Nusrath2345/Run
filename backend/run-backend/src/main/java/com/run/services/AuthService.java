package com.run.services;

import com.run.dto.LoginRequest;
import com.run.dto.RegisterRequest;
import com.run.models.User;
import com.run.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "EMAIL_TAKEN";
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);
        return "SUCCESS";
    }

    public String login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> user.getPassword().equals(request.getPassword()) ? "SUCCESS" : "WRONG_PASSWORD")
                .orElse("USER_NOT_FOUND");
    }
}