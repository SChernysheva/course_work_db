package org.example.sport_section.Utils;

import org.example.sport_section.Models.User;
import org.example.sport_section.Models.UserModelAuthorization;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;



public final class SecurityUtils {
    private SecurityUtils() {
        // Приватный конструктор, чтобы предотвратить создание экземпляра
    }
    public static void saveUserInCurrentSession(String email, String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            System.out.println("in");
            return auth.getName(); // или вернуть объект пользователя
        }
        return null;
    }
    public static void clearCurrentUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
    public static void deleteContext() {
        SecurityContextHolder.clearContext();
    }
}

