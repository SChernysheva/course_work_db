package org.example.sport_section.Utils;

import org.example.sport_section.Models.User;
import org.example.sport_section.Models.UserModelAuthorization;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public final class SecurityUtils {
    private SecurityUtils() {
        // Приватный конструктор, чтобы предотвратить создание экземпляра
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}

