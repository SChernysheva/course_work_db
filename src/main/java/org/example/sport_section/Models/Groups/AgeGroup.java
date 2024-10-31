package org.example.sport_section.Models.Groups;

import jakarta.persistence.*;

@Entity
@Table(name = "age_groups")
public class AgeGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ageGroup;

    public AgeGroup() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}
