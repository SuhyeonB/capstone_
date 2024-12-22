package com.engdiarytoon.server.user;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    User signup(String name, String email, String password);
    boolean isEmailRegistered(String email);
    Map<String, Object> signin(String email, String password);
    void signout(Long userId);
    void deleteUser(Long userId);
    void updateUser(Long userId, Map<String, Object> updates);
    void resetPassword(String email);
    Map<String, Object> kakaoLogin(String authorizationCode);
    Map<String, Object> googleLogin(String authorizationCode);
}
