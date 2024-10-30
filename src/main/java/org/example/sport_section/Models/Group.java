package org.example.sport_section.Models;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "group_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "coach_id", referencedColumnName = "id")
    private Coach coach;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private List<Schedule> schedules;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private LevelGroup levelGroup;

    @ManyToOne
    @JoinColumn(name = "age_id")
    private AgeGroup ageGroup;


    public LevelGroup getLevelGroup() {
        return levelGroup;
    }

    public void setLevelGroup(LevelGroup levelGroup) {
        this.levelGroup = levelGroup;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public Group() {

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllInfo() {
        return name + " " + ageGroup.getAgeGroup() + " " + levelGroup.getLevelGroup();
    }

}
