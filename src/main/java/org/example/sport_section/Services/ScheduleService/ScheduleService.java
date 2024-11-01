package org.example.sport_section.Services.ScheduleService;

import org.example.sport_section.Models.Groups.Schedule;
import org.example.sport_section.Models.Weekday.Weekday;
import org.example.sport_section.Repositories.IScheduleRepository;
import org.example.sport_section.Repositories.IWeekdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        return CompletableFuture.supplyAsync(weekdayRepository::findAll);
    }
}
