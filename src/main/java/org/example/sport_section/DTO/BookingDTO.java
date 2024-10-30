package org.example.sport_section.DTO;

import java.sql.Date;

public class BookingDTO {

    private String courtName;
    private Date date;
    private int hour;
    private int bookingId;
    private int userId;
    private int courtId;

    public BookingDTO(String courtName, Date date, int hour, int bookingId, int userId, int courtId) {
        this.userId = userId;
        this.courtId = courtId;
        this.bookingId = bookingId;
        this.courtName = courtName;
        this.date = date;
        this.hour = hour;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourtId() {
        return courtId;
    }

    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }
}
