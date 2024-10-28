package org.example.sport_section.Models;


import jakarta.persistence.*;

@Entity
@Table(name = "courts")
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String courtName;

    public Court(int id, String courtName) {
        this.id = id;
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
