package org.example.sport_section.Models.Users;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "passwords")
public class UserModelAuthorization {
    private String email;
    @Column(name = "hash_password")
    private String hashPassword;
    @Id
    @Column(name = "id")
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
