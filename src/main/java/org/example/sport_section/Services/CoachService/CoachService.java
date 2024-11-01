package org.example.sport_section.Services.CoachService;

import org.example.sport_section.Models.Users.Coach;
import org.example.sport_section.Repositories.CoachRepository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CoachService {
    private CoachRepository coachRepository;
    @Autowired
    public CoachService(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }
    @Async
    public CompletableFuture<List<Coach>> getCoaches() {
        return CompletableFuture.supplyAsync(() -> coachRepository.findAll());
    }

}
