package org.example.sport_section.Models;

public class UserModelAuthorization {
    private String email;
    private String hashPassword;
    private long userId;
    public UserModelAuthorization(String email, String hashPassword, long userId) {
        this.email = email;
        this.hashPassword = hashPassword;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UserModelAuthorization() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
}
