package org.example.sport_section.Validators;

import org.example.sport_section.Models.User;

import java.util.regex.Pattern;

public class UserValidator {


    // Метод для валидации email
    public static String validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || !pattern.matcher(email).matches() || email.length() > 50) {
            return "Некорректный email.";
        }
        return null;
    }

    // Метод для валидации телефона (phone)
    public static String validatePhone(String phone) {
        String phoneRegex = "^[0-9]{10,12}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        if (phone == null || !pattern.matcher(phone).matches()) {
            return ("Некорректный формат телефона");
        }
        return null;
    }
}

