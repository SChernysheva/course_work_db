package org.example.sport_section.Utils.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private static Optional<Collection<? extends GrantedAuthority>> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return Optional.of(userDetails.getAuthorities());
            }
        }
        return Optional.empty();
    }

    public static boolean isAdminOrHigher() {
        return getUserRoles().map(roles -> roles.stream()
                        .anyMatch(role -> role.getAuthority().equals("ADMIN") || role.getAuthority().equals("OWNER")))
                .orElse(false);
    }

    public static boolean isCoach() {
        return getUserRoles().map(roles -> roles.stream()
                        .anyMatch(role -> role.getAuthority().equals("COACH")))
                .orElse(false);
    }

    public static boolean isOwner() {
        return getUserRoles().map(roles -> roles.stream()
                        .anyMatch(role -> role.getAuthority().equals("OWNER")))
                .orElse(false);
    }

    public static List<String> getAllUserRoles() {
        return getUserRoles()
                .map(roles -> roles.stream()
                        .map(role -> role.getAuthority())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }



}

