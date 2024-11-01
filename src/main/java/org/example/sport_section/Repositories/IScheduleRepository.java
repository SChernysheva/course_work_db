package org.example.sport_section.Repositories;

import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Weekday.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT s FROM Schedule s WHERE s.weekday.name = :name AND s.court.id = :courtId")
    public List<Schedule> getScheduleOnWeekDayName(@Param("name") String name, @Param("courtId") int courtId);

    @Query(value = "SELECT s.weekday, GROUP_CONCAT(s) as schedules FROM Schedule s WHERE s.court_id = :courtId GROUP BY s.weekday", nativeQuery = true)
    List<Object[]> getGroupedSchedulesByWeekday(@Param("courtId") int courtId);

}

