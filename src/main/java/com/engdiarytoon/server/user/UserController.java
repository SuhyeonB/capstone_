package com.engdiarytoon.server.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.signup(
                    signupRequest.getName(),
                    signupRequest.getEmail(),
                    signupRequest.getPassword()
            );
            return ResponseEntity.ok("SUCCESS");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, Object> result = userService.signin(email, password);

        String status = (String) result.get("status");

        switch (status) {
            case "User not found" :
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(status);
            case "Invalid password" :
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(status);
            case "Success":
                return ResponseEntity.ok(result);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred.");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<String> updateUser(@PathVariable Long user_id, @RequestBody Map<String, Object> updates) {
        userService.updateUser(user_id, updates);
        return ResponseEntity.ok("User updated successfully");
    }

}