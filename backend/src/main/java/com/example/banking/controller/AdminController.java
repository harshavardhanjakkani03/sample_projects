package com.example.banking.controller;

import com.example.banking.dto.UserSummary;
import com.example.banking.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/pending-users")
    public ResponseEntity<List<UserSummary>> pendingUsers() {
        return ResponseEntity.ok(adminService.listPendingUsers());
    }

    @PostMapping("/users/{id}/approve")
    public ResponseEntity<UserSummary> approveUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adminService.approveUser(id));
    }
}
