package org.example.sport_section.Controllers;

import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Services.CourtService.AuthorizeService;
import org.example.sport_section.Utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/authorize")
public class AuthorizeController {
    private final AuthorizeService authorizeService;

    @Autowired
    public AuthorizeController(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }
    @GetMapping("/getHashPassword")
    public CompletableFuture<String> getHashPassword(@RequestParam String email) {
        return authorizeService.getHashPasswordForEmail(email);
    }
    @GetMapping("/getUser")
    public CompletableFuture<UserModelAuthorization> getUser(@RequestParam String email) {
        return authorizeService.getUser(email);
    }
    @PostMapping("/addUser")
    public void addUser(@RequestParam String email, @RequestParam String password, @RequestParam long userId) {
        authorizeService.saveEmail(email, password, userId);
    }

    @PostMapping("/addUserInSession")
    public ResponseEntity<Void> addUserInSession(@RequestParam String email, @RequestParam String password) {
        SecurityUtils.saveUserInCurrentSession(email, password);
        return ResponseEntity.ok().build();
    }
}