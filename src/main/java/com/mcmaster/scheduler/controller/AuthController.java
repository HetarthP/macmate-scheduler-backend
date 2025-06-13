package com.mcmaster.scheduler.controller;

import com.mcmaster.scheduler.model.User;
import com.mcmaster.scheduler.repository.UserRepository;
import com.mcmaster.scheduler.dto.LoginRequest;
import com.mcmaster.scheduler.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "https://macmate-scheduler-frontend.vercel.app")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.ok(userRepository.save(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = userOpt.get();

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getEmail());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(401).body("Invalid password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Login failed");
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, String>> protectedEndpoint(@RequestHeader("Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("message", "Missing or invalid Authorization header");
            return ResponseEntity.status(401).body(response);
        }

        String token = authHeader.substring(7); // remove "Bearer "

        try {
            String email = jwtService.extractEmail(token);
            response.put("message", "You are authenticated");
            response.put("email", email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }
    }
}
