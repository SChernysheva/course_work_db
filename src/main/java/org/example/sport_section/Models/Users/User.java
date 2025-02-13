package org.example.sport_section.Models.Users;


import jakarta.persistence.*;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Groups.Group;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Booking_court> bookings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Owner owner;

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Coach coach;

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Booking_court> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking_court> bookings) {
        this.bookings = bookings;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User(int id, String first_name, String last_name, String email, String phone, List<Booking_court> bookings, Admin admin) {
        this.id = id;
        this.admin = admin;
        this.bookings = bookings;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
    }
    public User() {}

    public User(String firstName, String lastName, String email, String phone) {
        this.first_name = firstName;
        this.last_name = lastName;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
