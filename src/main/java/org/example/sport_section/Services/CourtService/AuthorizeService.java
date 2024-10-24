package org.example.sport_section.Services.CourtService;

import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Reposiroties.AuthorizeRepository;
import org.example.sport_section.Reposiroties.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Service
public class AuthorizeService {
    private final AuthorizeRepository authorizeRepository;
    @Autowired
    public AuthorizeService(AuthorizeRepository authorizeRepository) {
        this.authorizeRepository = authorizeRepository;
    }

    @Async
    public CompletableFuture<String> getHashPasswordForEmail(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.getHashPasswordForEmail(email));
    }
    @Async
    public CompletableFuture<UserModelAuthorization> getUser(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.getUser(email));
    }

    public void saveEmail(String email, String password, long userId) {
        authorizeRepository.saveEmail(email, password, userId);
    }

    @Async
    public CompletableFuture<Boolean> isEmailExists(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.isEmailInDatabase(email));
    }

}
