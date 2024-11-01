package org.example.sport_section.Models.Weekday;

import jakarta.persistence.*;
import org.example.sport_section.Models.Groups.Schedule;

import java.util.List;

@Entity
@Table(name = "weekdays")
public class Weekday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "weekday")
    private String weekday;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "weekday")
    private List<Schedule> schedules;

    @Column(name = "num_of_day")
    private int numOfDay;

    public int getNumOfDay() {
        return numOfDay;
    }

    public void setNumOfDay(int numOfDay) {
        this.numOfDay = numOfDay;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
}
