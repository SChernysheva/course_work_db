package org.example.sport_section.Controllers.Court;


import org.example.sport_section.Models.Court;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courts")
public class CourtController {
    private final CourtService courtService;
    private final BookingCourtService bookingCourtService;

    @Autowired
    public CourtController(CourtService courtService, BookingCourtService bookingCourtService) {
        this.courtService = courtService;
        this.bookingCourtService = bookingCourtService;
    }
    @GetMapping
    public List<Court> getAllCourts() throws InterruptedException {
        System.out.println("controller getAllCourts");
        return courtService.getCourtsAsync().join();
    }
//    @GetMapping("/")
//    public Court getCourtById(@RequestParam long id){
//        return courtService.getCourtByIdAsync(id).join();
//    }

    @GetMapping("/getBooking")
    public List<Integer> getBookingTimeForCourt(@RequestParam long id, @RequestParam LocalDate date) {
        System.out.println("getBookingTimeForCourt");
        return bookingCourtService.getBookingTimeForCourtAsync(id, date).join();
    }

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
        List<Integer> bookingHours = bookingCourtService.getBookingTimeForCourtAsync(id, date).join();
        for (int i = startHour; i <= 22; i++) {
            if (!bookingHours.contains(i)) {
                availableHours.add(i);
            }
        }
        return availableHours;
    }

    @PostMapping("/addBooking")
    public Long addBookingTimeForCourt(@RequestParam int courtId, @RequestParam LocalDate date,
                                       @RequestParam int hour, @RequestParam int id) throws SQLException {
        System.out.println("addBookingTimeForCourt" + SecurityUtils.getCurrentUserEmail());
        return bookingCourtService.addBookingTimeForCourt(courtId, date, hour, id).join();
    }
}



