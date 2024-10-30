package org.example.sport_section.Models;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Time;


@Entity
@Table(name = "booking_courts")
public class Booking_court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne
    @JoinColumn(name = "court_id", referencedColumnName = "id")
    private Court court;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Date date;
    private int time;

    public Booking_court(Court court, User user, Date date, int time) {
        this.court = court;
        this.user = user;
        this.date = date;
        this.time = time;
    }
    public Booking_court(Court court, User user, int id, Date date, int time) {
        this.court = court;
        this.user = user;
        this.id = id;
        this.date = date;
        this.time = time;
    }

    public Booking_court() {

    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
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
