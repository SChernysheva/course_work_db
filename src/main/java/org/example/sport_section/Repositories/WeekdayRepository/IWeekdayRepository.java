package org.example.sport_section.Repositories.WeekdayRepository;

import org.example.sport_section.Models.Weekday.Weekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWeekdayRepository  extends JpaRepository<Weekday, Integer> {

}
