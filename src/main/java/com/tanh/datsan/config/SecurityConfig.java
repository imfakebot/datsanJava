package com.tanh.datsan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tanh.datsan.entity.Account;
import com.tanh.datsan.repository.AccountRepository;
import com.tanh.datsan.constant.Role;
import org.springframework.boot.CommandLineRunner;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (accountRepository.findByUsername("admin").isEmpty()) {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@datsan.com");
                admin.setRole(Role.ADMIN);
                accountRepository.save(admin);
            }
            if (accountRepository.findByUsername("testuser").isEmpty()) {
                Account user = new Account();
                user.setUsername("testuser");
                user.setPassword(passwordEncoder.encode("testuser"));
                user.setEmail("user@datsan.com");
                user.setRole(Role.CUSTOMER);
                accountRepository.save(user);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/pitch/**", "/uploads/**", "/api/upload/**", "/css/**", "/images/**", "/register", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
