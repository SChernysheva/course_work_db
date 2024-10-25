package org.example.sport_section.Services.UserService;


import org.example.sport_section.DataBase.DatabaseConfig;
import org.example.sport_section.Models.User;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Repositories.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    public CompletableFuture<List<User>> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> userRepository.getUsers());
    }
    @Async
    public CompletableFuture<Long> addUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> userRepository.addUser(user));
    }

    @Async
    public CompletableFuture<Void> deleteUserAsync(long userId) {
        userRepository.deleteUser(userId);
        return CompletableFuture.completedFuture(null);
    }



}
