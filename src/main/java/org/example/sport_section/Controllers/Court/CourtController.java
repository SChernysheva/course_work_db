package org.example.sport_section.Controllers.Court;


import org.example.sport_section.Models.Court;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/courts")
public class CourtController {
    private final CourtService courtService;

    @Autowired
    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }
    @GetMapping
    public List<Court> getAllCourts() {
        return courtService.getCourtsAsync().join();
    }
//    @GetMapping("/")
//    public Court getCourtById(@RequestParam long id){
//        return courtService.getCourtByIdAsync(id).join();
//    }

    @GetMapping("/getBooking")
    public List<Integer> getBookingTimeForCourt(@RequestParam long id, @RequestParam LocalDate date) {
        System.out.println("getBookingTimeForCourt");
        return courtService.getBookingTimeForCourtAsync(id, date).join();
    }
//
//    @GetMapping("/isBooking")
//    public Boolean isBookingTimeForCourt(@RequestParam long id, @RequestParam LocalDate date, @RequestParam int hour) {
//        return courtService.isBookingTimeForCourt(id, date, hour).join();
//    }

    @GetMapping("/getAvailableTime")
    public List<Integer> getAviableTimeForCourt(@RequestParam long id, @RequestParam LocalDate date) {
        List<Integer> availableHours = new ArrayList<>();
        if (LocalDate.now().isAfter(date)) {
            //todo
        }
        int startHour = 7;
        if (date.isEqual(LocalDate.now())) {
            LocalTime currentTime = LocalTime.now();
            startHour = currentTime.getHour() + 1;
        }
        List<Integer> bookingHours = courtService.getBookingTimeForCourtAsync(id, date).join();
        for (int i = startHour; i <= 22; i++) {
            if (!bookingHours.contains(i)) {
                availableHours.add(i);
            }
        }
        return availableHours;
    }

    @PostMapping("/addBooking")
    public Long addBookingTimeForCourt(@RequestParam long courtId, @RequestParam LocalDate date,
                                       @RequestParam int hour) throws SQLException {
        return courtService.addBookingTimeForCourt(courtId, date, hour).join();
    }
}



