package com.netflix_plus_plus.cms.models;

public class LoginResponse {
    private String token;
    private User user;
    private String error;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess(){
        return token != null && user != null;
    }

    public boolean isAdmin(){
        return user != null && "Admin".equalsIgnoreCase(user.getRole());
    }
}