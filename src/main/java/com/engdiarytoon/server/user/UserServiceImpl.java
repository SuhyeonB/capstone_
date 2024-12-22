package com.engdiarytoon.server.user;


import com.engdiarytoon.server.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final KakaoApi kakaoApi;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, MailService mailService,JwtUtil jwtUtil, KakaoApi kakaoApi) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.jwtUtil = jwtUtil;
        this.kakaoApi = kakaoApi;
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public User signup(String name, String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .name(name)
                .email(email)
                .password(hashedPassword)
                .deleteFlag(false)
                .oauthProvider(null)
                .role(Role.USER)
                .build();
        try {
            User savedUser = userRepository.save(user);
            System.out.println("User saved with ID: " + savedUser.getUserId());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to create user", e);
        }
    }

    @Override
    public Map<String, Object> signin(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {   // User not found case
            response.put("status", "User not found");
            return response;
        }

        if (user.get().isDeleteFlag()) {
            response.put("status", "already deleted");
            return response;
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) { // Invalid password case
            response.put("status", "Invalid password");
            return response;
        }

        // Success case: Generate JWT tokens
        String accessToken = jwtUtil.generateToken(user.get().getUserId(), "BASIC");
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getUserId());
        jwtUtil.storeRefreshToken(user.get().getUserId(), refreshToken);

        response.put("status", "Success");
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.get().getUserId());
        response.put("username", user.get().getName());

        return response;
    }

    @Override
    public void signout(Long userId)
    {
        jwtUtil.removeRefreshToken(userId);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setDeleteFlag(true);
            userRepository.save(existingUser);
            System.out.println("User marked as deleted ID: " + userId);
        } else {
            throw new IllegalStateException("User not found with userId: " + userId);
        }
    }

    @Override
    @Transactional
    public void updateUser(Long userId, Map<String, Object> updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updates.containsKey("name")) user.setName((String) updates.get("name"));
        if (updates.containsKey("password")) {// update Password
            String hashedPassword = passwordEncoder.encode((String) updates.get("password"));
            user.setPassword(hashedPassword);
        }

        userRepository.save(user);
    }

    private Map<String, Object> generateTokens(User user) {
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", jwtUtil.generateToken(user.getUserId(), "SNS"));
        tokens.put("refreshToken", jwtUtil.generateRefreshToken(user.getUserId()));
        return tokens;
    }

    /* Kakao */
    @Override
    public Map<String, Object> kakaoLogin(String authorizationCode) {
        try {
            // 1. Request access token from kakao
            String kakaoAccessToken = kakaoApi.getAccessToken(authorizationCode);

            // 2. fetch user info
            KakaoUser kakaoUser = kakaoApi.getUserInfo(kakaoAccessToken);

            // 3. check if user already exists in the database
            Optional<User> existingUser = userRepository.findByEmail(kakaoUser.getEmail());
            if(existingUser.isPresent()) {
                return generateTokens(existingUser.get());
            } else {
                // 4. not exist, register new user in the database
                User newUser = User.builder()
                        .name(kakaoUser.getNickname())
                        .email(kakaoUser.getEmail())
                        .oauthProvider("KAKAO")
                        .role(Role.USER)
                        .build();
                userRepository.save(newUser);
                return generateTokens(newUser);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to log in with Kakao", e);
        }
    }

    @Override
    public Map<String, Object> googleLogin(String authorizationCode) {
        return null;
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email" + email + "not found"));

        String newPassword = PasswordGenerator.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        userRepository.save(user);

        mailService.sendNewPassword(user.getEmail(), "Your New Password", newPassword);
    }
}