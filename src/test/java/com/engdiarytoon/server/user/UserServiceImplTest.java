package com.engdiarytoon.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private User testUser;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .userId(1L)
                .name("jane doe")
                .email("jane@example.com")
                .password("hashedpasswordz")
                .role(Role.USER)
                .build();
    }

    /*
    @Test
    @DisplayName("회원가입 - 인증코드 생성")
    void makeVeriticationCode() {
        // given
        String email = "test@example.com";
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // when
        String code = userService.sendVeriticationCode(email);
        
        // then
        assertNotNull(code);
        assertEquals(4, code.length(), "Code should be 4 digit long");
        System.out.println("code = " + code);
    }*/

    @Test
    @DisplayName("회원가입")
    void signup() {
        // given
        String name = "John Doe", email = "john1doe@example.com";
        String password = "password123!";
        String hashedPassword = "hashedpassword123!";

        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User user = userService.signup(name, email, password);

        // then
        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(hashedPassword, user.getPassword());
        assertEquals(Role.USER, user.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // 로그인 tests
    @Test
    @DisplayName("로그인 - 존재하지 않는 회원")
    void signin_UserNotFound() {
        // given
        when(userRepository.findByEmail("unknown@example.com")).thenReturn (Optional.empty());

        // when
        Map<String, Object> response = userService.signin("unknown@example.com", "password");

        // then
        assertEquals("User not found", response.get("status"));
    }

    @Test
    @DisplayName("로그인 - 잘못된 정보(비밀번호 틀림)")
    void signin_InvalidInfo() {
        // given
        when(userRepository.findByEmail("jane@example.com")).thenReturn (Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        // when
        Map<String, Object> response = userService.signin("jane@example.com", "wrongPassword");

        // then
        assertEquals("Invalid password", response.get("status"));
    }

    @Test
    @DisplayName("로그인-success")
    void sign(){
        // given
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(1L, "BASIC")).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(1L)).thenReturn("refreshToken");

        // when
        Map<String, Object> response = userService.signin("jane@example.com", "password");

        // then
        assertEquals("Success", response.get("status"));
        assertEquals("accessToken", response.get("accessToken"));
        assertEquals("refreshToken", response.get("refreshToken"));
    }

    @Test
    void signout() {
    }

    @Test
    void deleteUser() {
    }
}