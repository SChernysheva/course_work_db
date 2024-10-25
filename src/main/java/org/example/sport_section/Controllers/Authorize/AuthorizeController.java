package org.example.sport_section.Controllers.Authorize;

import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Services.CourtService.AuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String getHashPassword(@RequestParam String email) {
        return authorizeService.getHashPasswordForEmailAsync(email).join();
    }
    @GetMapping("/getUser")
    public UserModelAuthorization getUser(@RequestParam String email) {
        System.out.println("email " + email);
        return authorizeService.getUserAsync(email).join();
    }
    @PostMapping("/addUser")
    public void addUser(@RequestParam String email, @RequestParam String password, @RequestParam long userId) {
        authorizeService.saveEmailAsync(email, password, userId).join();
    }
}