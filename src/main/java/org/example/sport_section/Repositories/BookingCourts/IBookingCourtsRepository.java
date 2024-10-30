package org.example.sport_section.Repositories.BookingCourts;

import jakarta.persistence.LockModeType;
import org.example.sport_section.Models.Booking_court;
import org.example.sport_section.DTO.BookingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface IBookingCourtsRepository extends JpaRepository <Booking_court, Integer> {

    @Query(value = "SELECT time FROM booking_courts WHERE court_id = :id AND date = :date", nativeQuery = true)
    public List<Integer> getBookingHoursByCourt_idAndDate(@Param("id") long id, @Param("date") Date date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking_court b WHERE b.court.id = :courtId AND b.time = :time AND b.date = :date")
    Booking_court findByCourtIdAndBookingTime(@Param("courtId") long courtId, @Param("date") Date date, @Param("time") int time);

    @Query(value = "select * from booking_courts where user_id = :id", nativeQuery = true)
    List<Booking_court> findByUserId(@Param("id") long userId);


    @Query("SELECT new org.example.sport_section.DTO.BookingDTO(c.courtName, bk.date, bk.time, bk.id, bk.user_id, bk.court.id) " +
            "FROM Booking_court bk JOIN bk.court c " +
            "WHERE bk.user_id = :id ORDER BY bk.date DESC, bk.time DESC")
    List<BookingDTO> getBookingViewsByUserId(@Param("id") long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM booking_courts WHERE id = :bookingId", nativeQuery = true)
    Integer deleteById(@Param("bookingId") long bookingId);
}
