package com.familytree.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // Đừng quên dòng này
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // <--- QUAN TRỌNG NHẤT: Thiếu cái này là code không chạy
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Cách viết mới cho Spring Boot 3
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Cho phép tất cả
            );
        return http.build();
    }
}