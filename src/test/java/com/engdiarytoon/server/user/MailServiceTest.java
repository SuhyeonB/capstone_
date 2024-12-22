package com.engdiarytoon.server.user;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MailServiceTest {

    private final JavaMailSender javaMailSender = Mockito.mock(JavaMailSender.class);
    private final RedisTemplate<String, String> redisTemplate = Mockito.mock(RedisTemplate.class);
    private final MailService mailService = new MailServiceImpl(javaMailSender, redisTemplate);

    @Test
    void testSendSimpleEmail() {
        String testEmail = "test@email.com";

        // Mock opsForValue
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act: Send verification email
        mailService.sendVerificationEmail(testEmail);

        // Assert: Verify email sending
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly(testEmail);
        assertThat(sentMessage.getSubject()).isEqualTo("Verification code");
        assertThat(sentMessage.getText()).contains("Your verification code is:");

        // Assert: Verify RedisTemplate interaction
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(valueOperations, times(1)).set(
                keyCaptor.capture(),
                valueCaptor.capture(),
                eq(Duration.ofSeconds(310))
        );

        assertThat(keyCaptor.getValue()).isEqualTo(testEmail);
        assertThat(valueCaptor.getValue()).matches("\\d{4}"); // Ensure it matches a 4-digit code
    }
}
