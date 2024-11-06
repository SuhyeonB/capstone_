package com.engdiarytoon.server.security;

import com.engdiarytoon.server.user.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (token != null) {
            try {
                Claims claims = jwtUtil.validateToken(token);

                if (jwtUtil.isRefreshToken(claims)) {
                    if (jwtUtil.isTokenExpired(claims)) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Refresh token");
                        return;
                    }

                    // Generate new access token and send it in the response body
                    Long userId = Long.valueOf(claims.getSubject());
                    String newAccessToken = jwtUtil.generateToken(userId, "BASIC");

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"accessToken\": \"" + newAccessToken + "\"}");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return; // End response here for refresh token case
                }

                // Continue with access token validation if it's not a refresh token
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token expired");
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        } else {
            filterChain.doFilter(request, response); // No token provided; continue
        }
    }


    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
