package org.example.sport_section.Services.CourtService;

import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Repositories.BookingCourts.IBookingCourtsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
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

    @Async //here
    public CompletableFuture<List<Time>> getBookingTimeForCourtAsync(long id, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.getBookingHoursByCourt_idAndDate(id, Date.valueOf(date)));
    }

    @Async
    public CompletableFuture<List<Booking_court>> getBookings() {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.findAll());
    }

    @Async //here
    public CompletableFuture<List<Booking_court>> getBookingsForUserAsync(long userId) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.findByUserId(userId));
    }

    @Transactional
    @Async
    public CompletableFuture<Integer> addBookingTimeForCourt(Booking_court bk) {
        Booking_court existingBooking = bookingCourtsRepository.findByCourtIdAndBookingTime(bk.getCourt().getId(), bk.getDate(), bk.getTime());
        if (existingBooking != null) {
            throw new IllegalStateException("Корт уже забронирован на это время.");
        }
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.save(bk).getId());
    }

    @Async //here
    public CompletableFuture<Integer> deleteBookingAsync(int bookingId) {
        return CompletableFuture.supplyAsync(() -> bookingCourtsRepository.deleteById(bookingId));
    }

}
