package org.example.sport_section.Models.Users;

import jakarta.persistence.*;
import org.example.sport_section.Models.Groups.Group;

import java.util.List;

@Entity
@Table(name = "coaches")
public class Coach {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "coach")
    private List<Group> groups;

    public Coach(User user, int id) {
        this.user = user;
        this.id = id;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Coach() {

    }

    public User getUser() {
        return user;
    }

    public void setUser_id(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
