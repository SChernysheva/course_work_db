package org.example.sport_section.DTO;

import java.sql.Date;

public class BookingsForAdminDTO {
    private String userEmail;
    private String userName;
    private String userLastName;
    private Date bookingDate;
    private int hour;
    private int bookingId;
    private String courtName;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
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
    public String getCourtName() {
        return courtName;
    }
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }


    public BookingsForAdminDTO(String userEmail, String userName, String userLastName, Date bookingDate, int hour,
                               int bookingId, String courtName, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userLastName = userLastName;
        this.bookingDate = bookingDate;
        this.hour = hour;
        this.courtName = courtName;
        this.bookingId = bookingId;
    }
}
