package org.example.sport_section.Validators;

import org.example.sport_section.Models.User;

import java.util.regex.Pattern;

public class UserValidator {

    // Метод для валидации фамилии (last name)
    public static void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty() || lastName.length() > 50) {
            throw new IllegalArgumentException("Last name must be a non-empty string with a maximum length of 50 characters.");
        }
    }

    // Метод для валидации имени (first name)
    public static void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty() || firstName.length() > 50) {
            throw new IllegalArgumentException("First name must be a non-empty string with a maximum length of 50 characters.");
        }
    }

    // Метод для валидации email
    public static void validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || !pattern.matcher(email).matches() || email.length() > 50) {
            throw new IllegalArgumentException("Некорректный email.");
        }
    }

    // Метод для валидации телефона (phone)
    public static void validatePhone(String phone) {
        String phoneRegex = "^[0-9]{10,12}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        if (phone == null || !pattern.matcher(phone).matches()) {
            throw new IllegalArgumentException("Некорректный формат телефона");
        }
    }
}

