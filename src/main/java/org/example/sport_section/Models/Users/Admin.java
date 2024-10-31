package org.example.sport_section.Models.Users;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //private int user_id;

    public Admin(int id, User user) {
        this.id = id;
        this.user = user;
    }

    public Admin() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser_id() {
        return user;
    }

    public void setUser_id(User user_id) {
        this.user = user;
    }
}
