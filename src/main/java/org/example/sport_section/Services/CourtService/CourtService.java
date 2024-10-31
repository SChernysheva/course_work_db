package org.example.sport_section.Services.CourtService;

import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Repositories.Courts.ICourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Service
public class CourtService {
    private final ICourtRepository courtRepository;
    @Autowired
    public CourtService(ICourtRepository courtRepository) {
        this.courtRepository = courtRepository;
    }

    @Async
    public CompletableFuture<List<Court>> getCourtsAsync() {
        return CompletableFuture.supplyAsync(courtRepository::findAll);
    }
    @Async
    public CompletableFuture<Optional<Court>> getCourtByIdAsync(int id) {
        return CompletableFuture.supplyAsync(() -> courtRepository.findById(id));
    }

}
