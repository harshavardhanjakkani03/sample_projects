package com.example.banking.service;

import com.example.banking.dto.RegisterRequest;
import com.example.banking.model.User;
import com.example.banking.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest req) {
        User u = new User();
        u.setEmail(req.getEmail());
        u.setName(req.getName());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setApproved(false);
        return userRepository.save(u);
    }
}
