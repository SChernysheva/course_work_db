package org.example.sport_section.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Time;


@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int court_id;
    private String day_week;
    private Time time;
    private int group_id;

    public Schedule(int id, int court_id, String day_week, Time time, int group_id) {
        this.id = id;
        this.court_id = court_id;
        this.day_week = day_week;
        this.time = time;
        this.group_id = group_id;
    }

    public Schedule() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourt_id() {
        return court_id;
    }

    public void setCourt_id(int court_id) {
        this.court_id = court_id;
    }

    public String getDay_week() {
        return day_week;
    }

    public void setDay_week(String day_week) {
        this.day_week = day_week;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}
