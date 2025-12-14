package com.netflix_plus_plus.cms.models;

import com.google.gson.annotations.SerializedName;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    //private String passwordHash;
    //private LocalDateTime createdAt;
    //private LocalDateTime lastLogin;
    private String role;
    @SerializedName("is_active")
    private Boolean isActive;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    //public String getPasswordHash() { return passwordHash; }
    //public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    //public LocalDateTime getCreatedAt() { return createdAt; }
    //public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    //public LocalDateTime getLastLogin() { return lastLogin; }
    //public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public Boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "'}";
    }
}