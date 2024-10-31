package org.example.sport_section.Services.CourtService;

import jakarta.persistence.EntityNotFoundException;
import org.example.sport_section.Exceptions.NotFoundException;
import org.example.sport_section.Exceptions.ValueAlreadyExistsException;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Repositories.BookingCourts.IBookingCourtsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
public class BookingCourtService {

    private IBookingCourtsRepository bookingCourtsRepository;

    @Autowired
    BookingCourtService(IBookingCourtsRepository bookingCourtsRepository) {
        this.bookingCourtsRepository = bookingCourtsRepository;
    }

    @Async
    public CompletableFuture<List<Time>> getBookingTimeForCourtAsync(long id, LocalDate date) {
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
    
    @Transactional
    @Async
    public CompletableFuture<Booking_court> addBookingTimeForCourt(Booking_court bk) throws CompletionException {
        return CompletableFuture.supplyAsync(() -> {
            return bookingCourtsRepository.save(bk);
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new ValueAlreadyExistsException(ex.getMessage()));
            }
            return result;
        });
    }

    @Async
    public CompletableFuture<Void> deleteBookingAsync(int id) throws CompletionException {
        return CompletableFuture.runAsync(() -> {
            if (!bookingCourtsRepository.existsById(id)) {
                throw new EntityNotFoundException("Booking with id " + id + " not found.");
            }
            bookingCourtsRepository.deleteById(id);
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new NotFoundException(ex.getMessage()));
            }
            return result;
        });
    }


}
