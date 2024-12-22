package com.engdiarytoon.server.config;

import com.engdiarytoon.server.security.JwtAuthenticationFilter;
import com.engdiarytoon.server.user.CustomOAuth2UserService;
import com.engdiarytoon.server.user.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                        .requestMatchers("/api/users", "/api/users/signin", "/api/oauth/**",
                                "/api/emails/send-email", "/api/emails/verify-code",
                                "/api/assistant/analyze"
                        ).permitAll()

                        // Protected endpoints
                        .requestMatchers("/api/user/{userId}").authenticated()
                        .requestMatchers("/api/oauth/signin/google").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Add the JWT Authentication filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            System.out.println("Login Successful: " + authentication.getName());
                            response.sendRedirect("http://localhost:3000");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.err.println("Login Failed: " + exception.getMessage());
                            response.sendRedirect("http://localhost:3000/signin");
                        })
                );



        return http.build();
    }
}