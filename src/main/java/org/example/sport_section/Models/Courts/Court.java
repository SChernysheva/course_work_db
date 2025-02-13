package org.example.sport_section.Models.Courts;


import jakarta.persistence.*;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Images.CourtImage;
import org.example.sport_section.Models.Users.Admin;

import java.util.List;

@Entity
@Table(name = "courts")
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String courtName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "court")
    private List<Schedule> schedules;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "court")
    private List<Booking_court> bookings;

    @OneToOne(mappedBy = "court", cascade = CascadeType.ALL)
    private CourtImage courtImage;

    public CourtImage getCourtImage() {
        return courtImage;
    }

    public void setCourtImage(CourtImage courtImage) {
        this.courtImage = courtImage;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Booking_court> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking_court> bookings) {
        this.bookings = bookings;
    }

    public Court(int id, String courtName, List<Booking_court> bookings) {
        this.id = id;
        this.bookings = bookings;
        this.courtName = courtName;
    }

    public Court() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }
}
