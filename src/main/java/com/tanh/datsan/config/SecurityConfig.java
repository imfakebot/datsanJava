package com.tanh.datsan.config;

import com.tanh.datsan.security.UserDetailsServiceImpl;
import com.tanh.datsan.entity.Account;
import com.tanh.datsan.repository.AccountRepository;
import com.tanh.datsan.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private com.tanh.datsan.security.LoginSuccessHandler loginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
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
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login", "/register", "/verify", "/login-otp", "/css/**", "/js/**", "/images/**", "/static/**", "/pitch/**", "/uploads/**", "/api/upload/**", "/error").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("datsan_super_secret_key_2026")
                        .rememberMeParameter("remember") // Phù hợp với name="remember" trong login.html
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // Nhớ trong 7 ngày
                        .userDetailsService(userDetailsService)
                );

        return http.build();
    }
}
