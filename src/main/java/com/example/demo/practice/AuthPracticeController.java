package com.example.demo.practice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Practice controller for auth endpoints.
 */
@RestController
@RequestMapping("/practice/auth")
public class AuthPracticeController {

    private final AuthPracticeService authPracticeService;

    public AuthPracticeController(AuthPracticeService authPracticeService) {
        this.authPracticeService = authPracticeService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok(authPracticeService.getDummyToken());
    }
}
