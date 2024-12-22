package com.engdiarytoon.server.user;

public interface MailService {
    void sendVerificationEmail(String email);
    boolean verifyCode(String email, String code);
    public void sendNewPassword(String email, String subject, String newPassword);
}