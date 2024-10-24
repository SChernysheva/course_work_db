package org.example.sport_section.Services.CourtService;

import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.User;
import org.example.sport_section.Reposiroties.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public CompletableFuture<List<Court>> getCourts() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return courtRepository.getCourts();
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching courts", e);
            }
        });
    }
}
