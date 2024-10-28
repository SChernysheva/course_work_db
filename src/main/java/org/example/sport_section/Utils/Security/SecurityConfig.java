package org.example.sport_section.Utils.Security;

import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Repositories.Authorize.IAuthorizeRepository;
import org.example.sport_section.Utils.JWT.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(@Lazy JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return auth.build();
    }

    @Bean
    public UserDetailsService userDetailsService(IAuthorizeRepository authorizeRepository) {
        return email -> {
            Optional<UserModelAuthorization> userEntity = authorizeRepository.getByEmail(email);
            if (userEntity.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            return User.withUsername(userEntity.get().getEmail())
                    .password(userEntity.get().getHashPassword())
                    .roles("USER") // Добавьте роли, если необходимо
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("register").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        //.loginPage("/loginPage")
                        .successHandler((request, response, authentication) -> {
                            // Перенаправление на нужный путь
                            response.sendRedirect("");
                        })
                )
                .logout(Customizer.withDefaults());
        // Добавляем JwtRequestFilter в качестве фильтра
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

