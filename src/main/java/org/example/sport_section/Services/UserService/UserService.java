package org.example.sport_section.Services.UserService;


import org.example.sport_section.Models.Admin;
import org.example.sport_section.Models.User;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Repositories.User.IAdminRepository;
import org.example.sport_section.Repositories.User.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private IUserRepository userRepository;
    private IAdminRepository adminRepository;
    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Async
    public CompletableFuture<List<User>> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll());
    }
    @Async
    public CompletableFuture<Integer> addUserAsync(User user) {
        System.out.println("addUserAsync" + user.getEmail());
        return CompletableFuture.supplyAsync(() -> userRepository.save(user).getId());
    }

    @Async
    public CompletableFuture<Void> deleteUserAsync(int userId) {
        userRepository.deleteById(userId);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<User> getUserAsync(String email) {
        System.out.println("getUserAsync" + email);
        return CompletableFuture.supplyAsync(() -> userRepository.findByEmail(email));
    }
    @Async
    public CompletableFuture<Optional<User>> getUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> userRepository.findById(id));
    }

    @Async
    public CompletableFuture<Admin> getAdminByUserIdAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> adminRepository.getAdmin(userId));
    }


}
