package org.example.sport_section.Repositories.BookingCourts;

import org.example.sport_section.Models.Booking_court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface IBookingCourtsRepository extends JpaRepository <Booking_court, Integer> {
    @Query(value = "SELECT time FROM booking_courts WHERE court_id = :id AND date = :date", nativeQuery = true)
    public List<Integer> getBookingHoursByCourt_idAndDate(@Param("id") long id, @Param("date") Date date);
}
