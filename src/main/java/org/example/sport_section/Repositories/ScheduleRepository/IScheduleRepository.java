package org.example.sport_section.Repositories.ScheduleRepository;

import org.example.sport_section.Models.Groups.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;

@Repository
public interface IScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT s FROM Schedule s WHERE s.weekday.name = :name AND s.court.id = :courtId")
    public List<Schedule> getScheduleOnWeekDayName(@Param("name") String name, @Param("courtId") int courtId);

    @Query("SELECT s.time FROM Schedule s WHERE s.weekday.id = :id AND s.court.id = :courtId")
    public List<Time> getBookingTimeForDayWeek(@Param("courtId") int courtId, @Param("id") int weekDayId);
}

