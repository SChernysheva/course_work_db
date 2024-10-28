package org.example.sport_section.Utils.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // Возвращает email, так как мы используем email в качестве имени пользователя
            }
        }
        return null; // Если пользователь не аутентифицирован
    }

    public static void deleteAuth() {
        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUserEmail() != null;
    }

}

