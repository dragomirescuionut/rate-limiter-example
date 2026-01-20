package com.pm.ratelimiterexample;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequestMapping("/api/v1")
@RestController
public class ApiController {
    private final BucketRateLimiter rateLimiter;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password123";

    public ApiController(BucketRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request,
                                        HttpServletRequest servletRequest) {

        String clientIp = servletRequest.getRemoteAddr();

        if (!rateLimiter.tryConsume(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many login attempts. Please try again later.");
        }
        
        if (USERNAME.equals(request.getUsername()) && PASSWORD.equals(request.getPassword())) {
            return ResponseEntity.ok("Login successful!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        }
    }
}