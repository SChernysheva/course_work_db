package org.example.sport_section.Services.CourtService;

import org.example.sport_section.Models.Court;
import org.example.sport_section.Repositories.Courts.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Service
public class CourtService {
    private final CourtRepository courtRepository;
    @Autowired
    public CourtService(CourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    @Async
    public CompletableFuture<List<Court>> getCourtsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return courtRepository.getCourts();
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching courts", e);
            }
        });
    }
    @Async
    public CompletableFuture<Court> getCourtByIdAsync(long id) {
        return CompletableFuture.supplyAsync(() -> courtRepository.getCourtById(id));
    }

    @Async
    public CompletableFuture<List<Integer>> getBookingTimeForCourtAsync(long id, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return courtRepository.getBookingTimeForCourt(id, date);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
    public CompletableFuture<Long> addBookingTimeForCourt(long courtId, LocalDate date, int hour) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return courtRepository.addBookingTimeForCourt(courtId, date, hour);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
