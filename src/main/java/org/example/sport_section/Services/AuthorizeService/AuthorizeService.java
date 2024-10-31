package org.example.sport_section.Services.AuthorizeService;

import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Models.Users.UserModelAuthorization;
import org.example.sport_section.Repositories.Authorize.IAuthorizeRepository;
import org.example.sport_section.Utils.Security.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Service
public class AuthorizeService {
    private final IAuthorizeRepository authorizeRepository;
    @Autowired
    public AuthorizeService(IAuthorizeRepository authorizeRepository) {
        this.authorizeRepository = authorizeRepository;
    }

    @Async
    public CompletableFuture<Optional<UserModelAuthorization>> getUserAsync(String email) {
        return CompletableFuture.supplyAsync(() -> authorizeRepository.getByEmail(email));
    }

    @Async
    public CompletableFuture<UserModelAuthorization> saveEmailAsync(String email, String password, long userId) {
        String hashPassword = PasswordUtils.hashPassword(password);
        return CompletableFuture.supplyAsync(() -> authorizeRepository.save(new UserModelAuthorization(email, hashPassword, userId)));
    }

}
