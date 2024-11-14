package org.example.sport_section.Repositories.BookingCourts;

import jakarta.persistence.LockModeType;
import org.example.sport_section.DTO.CourtBookingDTO;
import org.example.sport_section.Models.Courts.Booking_court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface IBookingCourtsRepository extends JpaRepository <Booking_court, Integer>, CustomBookingCourtRepository{

    @Query(value = "select * from GET_BOOKING_HOURS_BY_COURT_ID_AND_DATE(:id, :date_)", nativeQuery = true)
    public List<Time> get_Booking_Hours_By_Court_id_And_Date(@Param("id") int court_id, @Param("date_") Date date_);


    @Query(value = "select * from GET_BOOKING_COURTS_BY_USER_ID(:user_id)", nativeQuery = true)
    List<Booking_court> findByUserId(@Param("user_id") int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM booking_courts WHERE id = :bookingId", nativeQuery = true)
    Integer deleteById(@Param("bookingId") long bookingId);

    @Query("SELECT new org.example.sport_section.DTO.CourtBookingDTO(b.user, null, b.time, c) " +
            "FROM Booking_court b " +
            "left join Court c ON b.court.id = c.id " +
            "WHERE c.id = :courtId AND b.date = :date")
    public List<CourtBookingDTO> findCourtBookingsByCourtIdAndDate(@Param("courtId") int courtId, @Param("date") Date date);


    @Query("SELECT new org.example.sport_section.DTO.CourtBookingDTO(null, s.group, s.time, c) " +
            "FROM Schedule s " +
            "left join Court c ON s.court.id = c.id " +
            "WHERE c.id = :courtId AND s.weekday.name = :nameOfDayWeek")
    public List<CourtBookingDTO> findCourtSchedulesByCourtIdAndDate(@Param("courtId") int courtId,
                                                                    @Param("nameOfDayWeek") String nameOfDayWeek);
}
