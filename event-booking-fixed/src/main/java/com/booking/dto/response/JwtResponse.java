package com.booking.dto.response;

public class JwtResponse {

    private String token;

    private String type = "Bearer";

    private Long userId;

    private String username;

    private String role;

    public JwtResponse() {
    }

    public JwtResponse(String token,
                       Long userId,
                       String username,
                       String role) {

        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }
}