package org.example.sport_section.Services.CourtService;

import org.example.sport_section.DTO.BookingsForAdminDTO;
import org.example.sport_section.Models.Booking_court;
import org.example.sport_section.Repositories.BookingCourts.IBookingCourtsRepository;
import org.example.sport_section.DTO.BookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Async
    public CompletableFuture<List<Booking_court>> getBookings() {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.findAll());
    }
    @Async
    public CompletableFuture<List<Booking_court>> getBookingsForUserAsync(long userId) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.findByUserId(userId));
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

    @Transactional
    public CompletableFuture<Booking_court> findBookingAsync(Long courtId, int time, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.findByCourtIdAndBookingTime(courtId, Date.valueOf(date), time));
    }

    @Transactional
    @Async
    public CompletableFuture<Integer> addBookingTimeForCourt(Booking_court bk) throws SQLException {
        Booking_court existingBooking = bookingCourtsRepository.findByCourtIdAndBookingTime(bk.getCourt().getId(), bk.getDate(), bk.getTime());
        if (existingBooking != null) {
            //return null;
            throw new IllegalStateException("Корт уже забронирован на это время.");
        }
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.save(bk).getId());
    }

    @Async
    public CompletableFuture<Integer> deleteBookingAsync(int bookingId) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.deleteById(bookingId));
    }

}
