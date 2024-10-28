package org.example.sport_section.Utils.WebClient;

import org.example.sport_section.Utils.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    private final JwtUtils jwtUtil;

    @Autowired
    public WebClientConfiguration(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter((request, next) -> {
                    // Получаем JWT токен текущего пользователя из SecurityContext
                    String token = extractTokenFromContext();
                    System.out.println("token: " + token);

                    // Создаем новый запрос с измененными заголовками
                    ClientRequest newRequest = ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .build();

                    return next.exchange(newRequest);
                });
    }


    private String extractTokenFromContext() {
        // берём объект аутентификации из SecurityContext
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Возвращаем JWT токен или другую информацию о текущем пользователе по необходимости
            return jwtUtil.generateToken(authentication.getName()); // Предполагаем, что у вас есть метод для генерации токена
        }
        return null;
    }
}
