package com.engdiarytoon.server.user;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {
    private final UserService userService;

    @Autowired
    public OAuthController(UserService userService) {
        this.userService = userService;
    }

    /* Kakao */
    @PostMapping("/signin/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try{
            Map<String, Object> result = userService.kakaoLogin(code);
            return ResponseEntity.ok(result);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to login with kakao");
        }
    }
}
