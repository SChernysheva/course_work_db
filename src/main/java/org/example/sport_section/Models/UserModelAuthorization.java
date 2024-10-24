package org.example.sport_section.Models;

public class UserModelAuthorization {
    private String email;
    private String hashPassword;
    public UserModelAuthorization(String email, String hashPassword) {
        this.email = email;
        this.hashPassword = hashPassword;
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
