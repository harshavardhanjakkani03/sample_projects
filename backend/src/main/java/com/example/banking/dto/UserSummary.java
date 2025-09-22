package com.example.banking.dto;

import java.time.OffsetDateTime;

public class UserSummary {
    private Long id;
    private String email;
    private String name;
    private boolean approved;
    private OffsetDateTime createdAt;

    public UserSummary() {}

    public UserSummary(Long id, String email, String name, boolean approved, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.approved = approved;
        this.createdAt = createdAt;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
