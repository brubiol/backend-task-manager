package com.example.demo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "test-secret-key-must-be-at-least-32-characters-long-for-hmac");

        Field expirationField = JwtUtil.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 86400000L);
    }

    @Test
    void generateToken_ReturnsValidToken() {
        String token = jwtUtil.generateToken("testuser", "USER");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractUsername_ReturnsCorrectUsername() {
        String token = jwtUtil.generateToken("testuser", "USER");

        String username = jwtUtil.extractUsername(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateToken("testuser", "USER");

        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertThat(jwtUtil.validateToken("invalid.token.here")).isFalse();
    }
}
