package org.example.sport_section.Services.UserService;


import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Repositories.User.IAdminRepository;
import org.example.sport_section.Repositories.User.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class UserService {
    private IUserRepository userRepository;
    private IAdminRepository adminRepository;

    @Autowired
    public UserService(IUserRepository userRepository, IAdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Async
    public CompletableFuture<List<User>> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll());
    }

    //here
    @Async
    public CompletableFuture<Integer> addUserAsync(User user) {
        return CompletableFuture.supplyAsync(() -> userRepository.save(user).getId());
    }

    //here!!!!
    @Async
    public CompletableFuture<Void> deleteUserAsync(int userId) {
        userRepository.deleteById(userId);
        return CompletableFuture.completedFuture(null);
    }

    @Async //here
    public CompletableFuture<User> getUserAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userRepository.findByEmail(email));
    }

    @Async
    public CompletableFuture<Optional<User>> getUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> userRepository.findById(id));
    }

    @Async
    public CompletableFuture<Integer> addAdminAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> adminRepository.save(userId))
                .handle((result, ex) -> {
                    if (ex != null) {
                        throw new CompletionException(ex);
                    }
                    return result;
                });
    }

    //here
    @Async
    public CompletableFuture<Integer> addUserIntoGroup(int userId, Integer groupId) {
        return CompletableFuture.supplyAsync( () -> userRepository.addUserIntoGroup(groupId, userId));
    }

    //here
    public CompletableFuture<User> updateUser(int id, User updatedUser) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        updatedUser.setId(id);
        return CompletableFuture.supplyAsync(() -> userRepository.save(updatedUser));
    }

}
