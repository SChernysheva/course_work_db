package org.example.sport_section.Controllers;

import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Services.CourtService.AuthorizeService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
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
    public CompletableFuture<String> getHashPassword(@RequestParam String email) throws SQLException {
        System.out.println("here");
        return authorizeService.getHashPasswordForEmail(email);
    }
    @GetMapping("/getUser")
    public CompletableFuture<UserModelAuthorization> getUser(@RequestParam String email) throws SQLException {
        System.out.println("here");
        return authorizeService.getUser(email);
    }
    @PostMapping("/addUser")
    public void getUser(@RequestParam String email, @RequestParam String password) throws SQLException {
        System.out.println("here");
        authorizeService.saveEmail(email, password);
    }
}