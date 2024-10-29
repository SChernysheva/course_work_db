package org.example.sport_section.Controllers.User;

import org.example.sport_section.Models.User;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.validation.Validator;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/validate")
    public String validate(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam String phone) {
        User user = userService.getUserAsync(email).join();
        if (user != null) {
            return "Пользователь с такой почтой уже существует";
        }
        String ans = UserValidator.validateEmail(email);
        if (ans != null) {
            return ans;
        }
        ans = UserValidator.validatePhone(phone);
        return (ans == null) ? "ok" : ans;
    }
    @PostMapping("/addUser")
    public Integer addUser(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                        @RequestParam String phone) {
        System.out.println("controller user");
        User user = new User(firstName, lastName, email, phone);
        return userService.addUserAsync(user).join();
    }

    @GetMapping("/getUser")
    public User getUser(@RequestParam String email) {
        return userService.getUserAsync(email).join();
    }
}
