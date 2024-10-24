package org.example.sport_section.Controllers;


import org.example.sport_section.Models.Court;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
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
    public CompletableFuture<List<Court>> getAllProducts() throws SQLException {
        System.out.println("controller: " + SecurityUtils.getCurrentUserEmail());
        return courtService.getCourts();
    }
}

