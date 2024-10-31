package org.example.sport_section.Models.Users;


import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int coach_id;
    private int group_id;
    private int user_id;

    public Player(int id, int coach_id, int group_id, int user_id) {
        this.id = id;
        this.coach_id = coach_id;
        this.group_id = group_id;
        this.user_id = user_id;
    }

    public Player() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoach_id() {
        return coach_id;
    }

    public void setCoach_id(int coach_id) {
        this.coach_id = coach_id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
