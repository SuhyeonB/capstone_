package com.engdiarytoon.server.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final StringRedisTemplate redisTemplate;

    private final String SECRET_KEY = "englishdiarytoonz+secretkeyXqHY41yu/fdsaegdh";
    private final long ACCESS_TOKEN_EXP = 3600000;   // 1 hour (1000 * 60 * 60)
    private final long REFRESH_TOKEN_EXP = 864000000;  // 10 days(1000 * 60 * 60 * 24 * 10)

    public JwtUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(Long userId, String loginType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("loginType", loginType);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch(SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }


    public boolean isRefreshToken(Claims claims) {  // RefreshToken 일 때, true 반환
        return !claims.containsKey("loginType");
    }

    public void storeRefreshToken(Long userId, String refreshToken) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("refreshToken:" + userId, refreshToken, 10, TimeUnit.DAYS); // 10일 동안 저장
    }

    public boolean validateRefreshToken(Long userId, String refreshToken) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedToken = ops.get("refreshToken:" + userId);
        return refreshToken.equals(storedToken);
    }

    public void removeRefreshToken(Long userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }

    public Authentication getAuthentication(Claims claims) {
        Long userId = claims.get("userId", Long.class);
        String loginType = claims.get("loginType", String.class);

        // 권한 설정 (예: BASIC 권한)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + loginType);

        // Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(authority));
    }
}
