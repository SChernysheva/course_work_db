package org.example.sport_section.Controllers;

import org.example.sport_section.Models.User;
import org.example.sport_section.Services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/addUser")
    public Long addUser(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                        @RequestParam String phone) throws SQLException {
        User user = new User(firstName, lastName, email, phone);
        return userService.addUser(user);
    }
}
