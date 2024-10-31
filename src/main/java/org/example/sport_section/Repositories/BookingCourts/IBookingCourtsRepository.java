package org.example.sport_section.Repositories.BookingCourts;

import jakarta.persistence.LockModeType;
import org.example.sport_section.Models.Courts.Booking_court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface IBookingCourtsRepository extends JpaRepository <Booking_court, Integer> {

    @Query(value = "SELECT time FROM booking_courts WHERE court_id = :id AND date = :date", nativeQuery = true)
    public List<Time> getBookingHoursByCourt_idAndDate(@Param("id") long id, @Param("date") Date date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking_court b WHERE b.court.id = :courtId AND b.time = :time AND b.date = :date")
    Booking_court findByCourtIdAndBookingTime(@Param("courtId") long courtId, @Param("date") Date date, @Param("time") Time time);

    @Query(value = "select * from booking_courts where user_id = :id", nativeQuery = true)
    List<Booking_court> findByUserId(@Param("id") long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM booking_courts WHERE id = :bookingId", nativeQuery = true)
    Integer deleteById(@Param("bookingId") long bookingId);

}
