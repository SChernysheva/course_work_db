package org.example.sport_section.Repositories.BookingCourts;

import org.example.sport_section.DTO.CourtBookingDTO;
import org.example.sport_section.Models.Courts.Booking_court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface IBookingCourtsRepository extends JpaRepository <Booking_court, Integer> {

    @Query(value = "select * from GET_BOOKING_HOURS_BY_COURT_ID_AND_DATE(:id, :date_)", nativeQuery = true)
    List<Time> get_Booking_Hours_By_Court_id_And_Date(@Param("id") int court_id, @Param("date_") Date date_);


    @Query(value = "select * from GET_BOOKING_COURTS_BY_USER_ID(:user_id)", nativeQuery = true)
    List<Booking_court> findByUserId(@Param("user_id") int userId);


    @Transactional
    @Procedure(value = "delete_booking_by_id")
    Void deleteBookingById(int bookingId);

    @Query("SELECT new org.example.sport_section.DTO.CourtBookingDTO(b.user, null, b.time, c) " +
            "FROM Booking_court b " +
            "left join Court c ON b.court.id = c.id " +
            "WHERE c.id = :courtId AND b.date = :date_")
    //@Query(value = "select * from GET_BOOKING_COURT_BY_ID_AND_DATE(:courtId, :date_)", nativeQuery = true)
    List<CourtBookingDTO> findCourtBookingsByCourtIdAndDate(int courtId, Date date_);


    @Query("SELECT new org.example.sport_section.DTO.CourtBookingDTO(null, s.group, s.time, c) " +
            "FROM Schedule s " +
            "left join Court c ON s.court.id = c.id " +
            "WHERE c.id = :courtId AND s.weekday.name = :nameOfDayWeek")
    List<CourtBookingDTO> findCourtSchedulesByCourtIdAndDate(@Param("courtId") int courtId,
                                                                    @Param("nameOfDayWeek") String nameOfDayWeek);
}
