package org.example.sport_section.Models;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Time;


@Entity
@Table(name = "booking_courts")
public class Booking_court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private int court_id;
    private int user_id;
    private Date date;
    private int time;

    public Booking_court(int court_id, int user_id, Date date, int time) {
        this.court_id = court_id;
        this.user_id = user_id;
        this.date = date;
        this.time = time;
    }
    public Booking_court(int court_id, int user_id, int id, Date date, int time) {
        this.court_id = court_id;
        this.user_id = user_id;
        this.id = id;
        this.date = date;
        this.time = time;
    }

    public Booking_court() {

    }

    public int getCourt_id() {
        return court_id;
    }

    public void setCourt_id(int court_id) {
        this.court_id = court_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
