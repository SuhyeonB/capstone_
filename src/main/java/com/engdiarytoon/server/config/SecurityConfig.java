package com.engdiarytoon.server.config;

import com.engdiarytoon.server.security.JwtAuthenticationFilter;
import com.engdiarytoon.server.user.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/signup", "api/users/signin").permitAll()

                        // protected endpoints
                        .requestMatchers("/api/user/{userId}").authenticated()

                        .anyRequest().authenticated() // All other requests need authentication
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),  UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}