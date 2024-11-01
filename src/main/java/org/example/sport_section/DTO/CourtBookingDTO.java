package org.example.sport_section.DTO;

import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Groups.Group;
import org.example.sport_section.Models.Users.User;

import java.sql.Time;

public class CourtBookingDTO {
    private User user;
    private Group group;
    private Time time;
    private Court court;

    public CourtBookingDTO(User user, Group group, Time time, Court court) {
        this.user = user;
        this.group = group;
        this.time = time;
        this.court = court;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }
}
