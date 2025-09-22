package com.example.banking.controller;

import com.example.banking.dto.RegisterRequest;
import com.example.banking.model.User;
import com.example.banking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User created = userService.register(req);
        return ResponseEntity.ok(created);
    }
}
