package org.example.sport_section.Services.CourtService;

import org.example.sport_section.Models.Booking_court;
import org.example.sport_section.Repositories.BookingCourts.IBookingCourtsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class BookingCourtService {

    private IBookingCourtsRepository bookingCourtsRepository;

    @Autowired
    BookingCourtService(IBookingCourtsRepository bookingCourtsRepository) {
        this.bookingCourtsRepository = bookingCourtsRepository;
    }

    @Async
    public CompletableFuture<List<Integer>> getBookingTimeForCourtAsync(long id, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.getBookingHoursByCourt_idAndDate(id, Date.valueOf(date)));
    }

    // Пример на Java с использованием JPA
    // Предполагается, что у вас есть сущность Reservation с полем timeSlot
//
//    @Transactional
//    public void bookCourt(long courtId, LocalDate date, int hour) {
//        // Проверьте, свободно ли время
//        Optional<Reservation> existingReservation = reservationRepository.findByTimeSlot(timeSlot);
//
//        if (existingReservation.isPresent()) {
//            throw new IllegalStateException("Время уже забронировано");
//        }
//
//        // Если свободно, создайте новую запись бронирования
//        Reservation reservation = new Reservation();
//        reservation.setUserId(userId);
//        reservation.setTimeSlot(timeSlot);
//        reservationRepository.save(reservation);
//    }


    @Async
    public CompletableFuture<Long> addBookingTimeForCourt(int courtId, LocalDate date, int hour, int userId) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            Booking_court bk = new Booking_court(courtId, userId, Date.valueOf(date), hour);
            return bookingCourtsRepository.save(bk).getId();
        });
    }
}
