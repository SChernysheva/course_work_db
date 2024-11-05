package org.example.sport_section.Services.UserService;


import jakarta.persistence.EntityNotFoundException;
import org.example.sport_section.Exceptions.NotFoundException;
import org.example.sport_section.Exceptions.ValueAlreadyExistsException;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Repositories.CoachRepository.CoachRepository;
import org.example.sport_section.Repositories.GroupRepository.GroupRepository;
import org.example.sport_section.Repositories.User.IAdminRepository;
import org.example.sport_section.Repositories.User.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class UserService {
    private IUserRepository userRepository;
    private IAdminRepository adminRepository;
    private GroupRepository groupRepository;
    private CoachRepository coachRepository;

    @Autowired
    public UserService(IUserRepository userRepository, IAdminRepository adminRepository, GroupRepository groupRepository,
                       CoachRepository coachRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.groupRepository = groupRepository;
        this.coachRepository = coachRepository;
    }

    @Async
    public CompletableFuture<List<User>> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> userRepository.findAll());
    }

    @Async //here
    public CompletableFuture<Integer> addUserAsync(User user) throws CompletionException{
        return CompletableFuture.supplyAsync(() -> {
            return userRepository.save(user).getId();
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new ValueAlreadyExistsException("Такая почта уже существует"));
            }
            return result;
        });
    }


    @Async
    public CompletableFuture<Void> deleteUserAsync(int id) throws CompletionException {
        return CompletableFuture.runAsync(() -> {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User with id " + id + " not found.");
            }
            userRepository.deleteById(id);
        }).handle( (result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new NotFoundException(ex.getMessage()));
            }
            return result;
        });
    }

    @Async
    public CompletableFuture<Void> deleteAdminAsync(int id) {
        return CompletableFuture.runAsync(() -> {
            if (!adminRepository.existsById(id)) {
                throw new RuntimeException("User with id " + id + " not found.");
            }
            adminRepository.deleteById(id);
        }).exceptionally(ex -> {
            throw new CompletionException(new NotFoundException(ex.getMessage()));
        });
    }



    @Async
    public CompletableFuture<Optional<User>> getUserAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userRepository.findByEmail(email));
    }

    @Async
    public CompletableFuture<Optional<User>> getUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> userRepository.findById(id));
    }

    @Async
    public CompletableFuture<Integer> addAdminAsync(int userId) throws CompletionException {
        return CompletableFuture.supplyAsync(() -> adminRepository.save(userId))
                .handle((result, ex) -> {
                    if (ex != null) {
                        throw new CompletionException(ex);
                    }
                    return result;
                });
    }

    @Async
    public CompletableFuture<Integer> addCoachAsync(int userId) throws CompletionException {
        return CompletableFuture.supplyAsync(() -> coachRepository.save(userId))
                .handle((result, ex) -> {
                    if (ex != null) {
                        throw new CompletionException(ex);
                    }
                    return result;
                });
    }
    @Async
    public CompletableFuture<Void> deleteCoachAsync(int id) {
        return CompletableFuture.runAsync(() -> {
            if (!coachRepository.existsById(id)) {
                throw new RuntimeException("User with id " + id + " not found.");
            }
            coachRepository.deleteById(id);
        }).exceptionally(ex -> {
            throw new CompletionException(new NotFoundException(ex.getMessage()));
        });
    }

    @Async
    public CompletableFuture<Void> addUserIntoGroup(int userId, Integer groupId) throws CompletionException {
        return CompletableFuture.runAsync(() -> {
            if (!userRepository.existsById(userId)) {
                throw new IllegalStateException("Пользователь не найден");
            }
            if (groupId != null && !groupRepository.existsById(groupId)) {
                throw new IllegalStateException("Группа не найдена");
            }
            userRepository.addUserIntoGroup(groupId, userId);
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new NotFoundException(ex.getMessage()));
            }
            return result;
        });
    }

    @Async
    public CompletableFuture<User> updateUser(int id, User updatedUser, String oldEmail) throws CompletionException {
        updatedUser.setId(id);
        return CompletableFuture.supplyAsync(() -> {
            if (!userRepository.existsById(id)) {
                throw new IllegalStateException("Пользователь не найдем");
            }
            Optional<User> existingEmail = userRepository.findByEmail(updatedUser.getEmail());
            if (existingEmail.isPresent() && !updatedUser.getEmail().equals(oldEmail)) {
                throw new IllegalStateException("Пользователь с такой почтой уже существует");
            }
            return userRepository.save(updatedUser);
        }).handle((result, ex) -> {
            if (ex != null) {
                throw new CompletionException(new NotFoundException(ex.getMessage()));
            }
            return result;
        });
    }

}
