package com.example.banking.service;

import com.example.banking.dto.UserSummary;
import com.example.banking.model.User;
import com.example.banking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummary> listPendingUsers() {
        List<User> pending = userRepository.findByApprovedFalse();
        return pending.stream()
                .map(u -> new UserSummary(u.getId(), u.getEmail(), u.getName(), u.isApproved(), u.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSummary approveUser(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        u.setApproved(true);
        userRepository.save(u);
        return new UserSummary(u.getId(), u.getEmail(), u.getName(), u.isApproved(), u.getCreatedAt());
    }
}
