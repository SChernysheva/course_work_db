package org.example.sport_section.Utils.Security;

import org.example.sport_section.Models.Users.UserModelAuthorization;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Repositories.Authorize.IAuthorizeRepository;
import org.example.sport_section.Repositories.User.IAdminRepository;
import org.example.sport_section.Repositories.User.IUserRepository;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "OWNER")
                        // .requestMatchers("/home").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            // Перенаправление на нужный путь
                            response.sendRedirect("");
                        })
                )
                .logout(Customizer.withDefaults());

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(IAuthorizeRepository authorizeRepository,
                                                 IUserRepository userRepository, IAdminRepository adminRepository) {
        return email -> {
            Optional<UserModelAuthorization> userEntity = authorizeRepository.getByEmail(email);
            if (userEntity.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (!userOpt.isPresent()) {
                throw new UsernameNotFoundException("User not found");
            }
            User user = userOpt.get();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("USER"));
            if (user.getAdmin() != null) {
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
            }
            if (user.getOwner() != null) {
                authorities.add(new SimpleGrantedAuthority("OWNER"));
            }
            if (user.getCoach() != null) {
                authorities.add(new SimpleGrantedAuthority("COACH"));
            }
            return new org.springframework.security.core.userdetails.User(
                    userEntity.get().getEmail(),
                    userEntity.get().getHashPassword(),
                    authorities
            );
        };
    }

}
