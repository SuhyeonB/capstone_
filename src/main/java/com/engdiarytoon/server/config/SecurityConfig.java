package com.engdiarytoon.server.config;

import com.engdiarytoon.server.security.JwtAuthenticationFilter;
import com.engdiarytoon.server.user.CustomOAuth2UserService;
import com.engdiarytoon.server.user.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(JwtUtil jwtUtil, CustomOAuth2UserService customOAuth2UserService) {
        this.jwtUtil = jwtUtil;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/signup", "/api/users/signin", "/api/oauth/**").permitAll()

                        // Protected endpoints
                        .requestMatchers("/api/user/{userId}").authenticated()

                        .anyRequest().authenticated()
                )
                // Add the JWT Authentication filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // Enables OAuth2 login (for Google and Kakao login)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:3000")
                        .failureUrl("http://localhost:3000/login")
                );

        return http.build();
    }
}