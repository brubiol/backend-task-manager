package com.example.demo.practice;

import org.springframework.stereotype.Component;

/**
 * Practice JWT utility placeholders.
 */
@Component
public class JwtPracticeUtil {

    public String generateToken(String username) {
        // TODO: replace with real JWT generation
        return "dummy-token-for-" + username;
    }

    public boolean validateToken(String token) {
        // TODO: replace with real token validation
        return false;
    }

    public String extractUsername(String token) {
        // TODO: replace with real token parsing
        return null;
    }
}
