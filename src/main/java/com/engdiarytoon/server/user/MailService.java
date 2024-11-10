package com.engdiarytoon.server.user;

public interface MailService {
    void sendVerificationEmail(String email);
    boolean verifyCode(String email, String code);
}