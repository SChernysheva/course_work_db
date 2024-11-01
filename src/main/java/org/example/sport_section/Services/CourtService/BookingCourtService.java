package org.example.sport_section.Services.CourtService;

import jakarta.persistence.EntityNotFoundException;
import org.example.sport_section.DTO.CourtBookingDTO;
import org.example.sport_section.Exceptions.NotFoundException;
import org.example.sport_section.Exceptions.ValueAlreadyExistsException;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Repositories.BookingCourts.IBookingCourtsRepository;
import org.example.sport_section.Repositories.ScheduleRepository.IScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
public class BookingCourtService {

    private IBookingCourtsRepository bookingCourtsRepository;
    private IScheduleRepository scheduleRepository;

    @Autowired
    BookingCourtService(IBookingCourtsRepository bookingCourtsRepository, IScheduleRepository scheduleRepository) {
        this.bookingCourtsRepository = bookingCourtsRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Async
    public CompletableFuture<List<Time>> getBookingTimeForCourtAsync(int id, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
            String dayweek = date.getDayOfWeek().toString().toLowerCase();
            List<Time> bookBySchedule = scheduleRepository.getScheduleOnWeekDayName(dayweek, id).stream().
                    map(Schedule::getTime).toList();
            List<Time> booksByUsers = bookingCourtsRepository.getBookingHoursByCourt_idAndDate(id, Date.valueOf(date));
            Set<Time> res = new HashSet<>(booksByUsers);
            res.addAll(bookBySchedule);
            return new ArrayList<>(res);
        });
    }

    @Async
    public CompletableFuture<List<Time>> getAviavleTimeForCourtAsync(int id, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
            String dayweek = date.getDayOfWeek().toString().toLowerCase();
            List<Time> bookBySchedule = scheduleRepository.getScheduleOnWeekDayName(dayweek, id).stream().
                    map(Schedule::getTime).toList();
            List<Time> booksByUsers = bookingCourtsRepository.getBookingHoursByCourt_idAndDate(id, Date.valueOf(date));
            Set<Time> resBook = new HashSet<>(booksByUsers);
            resBook.addAll(bookBySchedule);
            System.out.println(booksByUsers);
            System.out.println(bookBySchedule);
            int startHour = 7;
            if (date.isEqual(LocalDate.now())) {
                LocalTime currentTime = LocalTime.now();
                startHour = Math.max(7, currentTime.getHour() + 1);
            }
            ArrayList<Time> res = new ArrayList<>();
            for (int i = startHour; i <= 22; i++) {
                Time currentTime = Time.valueOf(String.format("%02d:00:00", i));
                if (!resBook.contains(currentTime)) {
                    res.add(currentTime);
                }
            }
            return res;
        });
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
                throw new EntityNotFoundException("Этого бронирования уже нет.");
            }
            bookingCourtsRepository.deleteById(id);
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new NotFoundException(ex.getMessage()));
            }
            return result;
        });
    }

    @Async
    public CompletableFuture<List<CourtBookingDTO>> getScheduleCourtOnDate(int courtId, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> {
            String dayweek = date.getDayOfWeek().toString().toLowerCase();
            List<CourtBookingDTO> resBook = bookingCourtsRepository.findCourtBookingsByCourtIdAndDate(courtId,
                    Date.valueOf(date));
            List<CourtBookingDTO> resSchedule = bookingCourtsRepository.findCourtSchedulesByCourtIdAndDate(courtId, dayweek);
            Comparator<CourtBookingDTO> comparator =  new Comparator<CourtBookingDTO>() {
                @Override
                public int compare(CourtBookingDTO o1, CourtBookingDTO o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            };
            List<CourtBookingDTO> res = new ArrayList<>(resBook);
            res.addAll(resSchedule);
            Collections.sort(res, comparator);
            return res;
        });
    }


}
