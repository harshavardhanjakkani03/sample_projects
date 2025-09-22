package com.example.banking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        String branch = System.getenv().getOrDefault("GIT_BRANCH", "unknown");
        String commit = System.getenv().getOrDefault("GIT_COMMIT", "unknown");
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "branch", branch,
                "commit", commit
        ));
    }
}
