package org.example.sport_section.Services.ScheduleService;

import jakarta.persistence.EntityNotFoundException;
import org.example.sport_section.Exceptions.NotFoundException;
import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Repositories.ScheduleRepository.IScheduleRepository;
import org.example.sport_section.Repositories.WeekdayRepository.IWeekdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class ScheduleService {
    private final IScheduleRepository scheduleRepository;
    private final IWeekdayRepository weekdayRepository;

    @Autowired
    public ScheduleService(IScheduleRepository scheduleRepository, IWeekdayRepository weekdayRepository) {
        this.weekdayRepository = weekdayRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Async
    public CompletableFuture<Optional<Schedule>> getScheduleOnWeekday(Weekday weekday) {
        return CompletableFuture.supplyAsync(() -> scheduleRepository.findById(weekday.getId()));
    }

    @Async
    public CompletableFuture<List<Schedule>> getScheduleOnWeekday(String weekday, int courtId) {
        return CompletableFuture.supplyAsync(() -> scheduleRepository.getScheduleOnWeekDayName(weekday, courtId));
    }

    @Async
    public CompletableFuture<List<Schedule>> getAllSchedules() {
        return CompletableFuture.supplyAsync(scheduleRepository::findAll);
    }

    @Async
    public CompletableFuture<List<Weekday>> getAllWeekdaysWithSchedules() {
        return CompletableFuture.supplyAsync(() -> {
            Comparator<Weekday> comparator =  new Comparator<Weekday>() {
                @Override
                public int compare(Weekday o1, Weekday o2) {
                    return o1.getNumOfDay() - o2.getNumOfDay();
                }
            };
            List<Weekday> res = weekdayRepository.findAll();
            Collections.sort(res, comparator);
            return res;
        });
    }

    @Async
    public CompletableFuture<List<Weekday>> getAllWeekdays() {
        return CompletableFuture.supplyAsync(weekdayRepository::findAll);
    }

    @Async
    public CompletableFuture<List<Time>> getAvailableTimeForNewSchedule(int dayWeekId, int courtId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Time> booking =  scheduleRepository.getBookingTimeForDayWeek(courtId, dayWeekId);
            List<Time> res = new ArrayList<>();
            for (int i = 7; i <= 20; i++) {
                Time currentTime = Time.valueOf(String.format("%02d:00:00", i));
                if (!booking.contains(currentTime)) {
                    res.add(currentTime);
                }
            }
            return res;
        });
    }

    @Async //here
    public CompletableFuture<Void> addSchedule(Schedule schedule) {
        return CompletableFuture.runAsync(() -> {
            scheduleRepository.save(schedule);
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new NotFoundException("Ошибка"));
            }
            return result;
        });
    }

    @Async //here
    public CompletableFuture<Void> deleteSchedule(int id) {
        return CompletableFuture.runAsync(() -> {
            scheduleRepository.deleteById(id);
        });
    }
}
