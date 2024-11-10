package com.engdiarytoon.server.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class MailServiceImpl implements MailService{
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, @Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendVerificationEmail(String email) {

        // generate a random 4-digit verification code
        String code = String.format("%04d", new Random().nextInt(9999));

        // Redis에 코드 저장, 만료 시간 5분 10초로 설정
        redisTemplate.opsForValue().set(email, code, Duration.ofSeconds(310));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return code.equals(storedCode);
    }
}
