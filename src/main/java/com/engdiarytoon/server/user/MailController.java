package com.engdiarytoon.server.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
public class MailController {
    private final MailService mailService;
    private final UserService userService;

    @Autowired
    public MailController(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        // 이메일 중복 확인
        if (userService.isEmailRegistered(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }
        mailService.sendVerificationEmail(email);
        return ResponseEntity.ok("verification code sent");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isVerified = mailService.verifyCode(email, code);
        if (isVerified) {
            return ResponseEntity.ok("Verification successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification code");
        }
    }
}
