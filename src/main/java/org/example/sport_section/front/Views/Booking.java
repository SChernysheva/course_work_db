package org.example.sport_section.front.Views;

import java.sql.Date;
import java.sql.Time;

public class Booking {
    private long id;
    private long court_id;
    private long user_id;
    private Date date;
    private Time time;

    public Booking(long id, long court_id, long user_id, Date date, Time time) {
        this.id = id;
        this.court_id = court_id;
        this.user_id = user_id;
        this.date = date;
        this.time = time;
    }
    public Booking () {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourt_id() {
        return court_id;
    }

    public void setCourt_id(long court_id) {
        this.court_id = court_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
