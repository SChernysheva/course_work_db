package org.example.sport_section.Services.CourtService;

import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Repositories.Authorize.AuthorizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
    public CompletableFuture<String> getHashPasswordForEmailAsync(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.getHashPasswordForEmail(email));
    }
    @Async
    public CompletableFuture<UserModelAuthorization> getUserAsync(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.getUser(email));
    }

    @Async
    public CompletableFuture<Void> saveEmailAsync(String email, String password, long userId) {
        authorizeRepository.saveEmail(email, password, userId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Boolean> isEmailExistsAsync(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.isEmailInDatabase(email));
    }

}
