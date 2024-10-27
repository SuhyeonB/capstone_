package com.engdiarytoon.server.user;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Map<String, String> verificationCodes = new HashMap<>();
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String sendVeriticationCode(String email) {
        // check if email already exists : 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            return "Email already exists";
        }
        // generate a random 4-digit verification code
        String code = String.format("%04d", new Random().nextInt(9999));
        // Store the code associated with the email
        verificationCodes.put(email, code);
        // I'll add Sending email (SMTP) later
        //System.out.println("email = " + email + " code : " + code);
        // return the code for front-end usage
        return code;
    }

    @Override
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
        String accessToken = jwtUtil.generateToken(user.get().getUserId(), "BASIC", null);
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getUserId());

        response.put("status", "Success");
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;
    }

    @Override
    public void signout(Long userId)
    {
        // signout logic (invalidate tokens if necessary)
    }

    @Override
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
}
