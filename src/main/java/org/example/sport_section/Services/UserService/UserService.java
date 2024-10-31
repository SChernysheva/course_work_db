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

//    @Async
//    public CompletableFuture<Admin> getAdminByUserIdAsync(int userId) {
//        return CompletableFuture.supplyAsync(() -> adminRepository.getAdmin(userId));
//    }

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

    @Async
    public CompletableFuture<Integer> addUserIntoGroup(int userId, Integer groupId) {
        return CompletableFuture.supplyAsync( () -> userRepository.addUserIntoGroup(groupId, userId));
    }

}
