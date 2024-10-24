package org.example.sport_section.Models;

public class Court {
    private int id;
    private String nameCourt;

    public Court(int id, String nameCourt) {
        this.id = id;
        this.nameCourt = nameCourt;
    }

    public Court() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCourt() {
        return nameCourt;
    }

    public void setNameCourt(String nameCourt) {
        this.nameCourt = nameCourt;
    }
}
