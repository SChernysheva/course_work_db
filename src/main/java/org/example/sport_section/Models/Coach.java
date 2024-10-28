package org.example.sport_section.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "coaches")
public class Coach {
    private int user_id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public Coach(int user_id, int id) {
        this.user_id = user_id;
        this.id = id;
    }

    public Coach() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
