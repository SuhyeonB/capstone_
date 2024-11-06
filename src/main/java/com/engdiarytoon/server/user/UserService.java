package com.engdiarytoon.server.user;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    User signup(String name, String email, String password);
    String sendVeriticationCode(String email);
    Map<String, Object> signin(String email, String password);
    void signout(Long userId);
    void deleteUser(Long userId);
}
