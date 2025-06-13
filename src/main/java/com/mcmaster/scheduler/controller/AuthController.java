package com.mcmaster.scheduler.controller;

import java.util.List;
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

@CrossOrigin(origins = "https://macmate.vercel.app")
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
    public Map<String, String> login(@RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            List<User> userList = userRepository.findAllByEmail(request.getEmail());

            if (userList.isEmpty()) {
                response.put("message", "User not found");
                return response;
            }

            User user = userList.get(0);

            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getEmail());
                response.put("token", token);
            } else {
                response.put("message", "Invalid password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Login failed");
        }

        return response;
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, String>> protectedEndpoint(@RequestHeader("Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("message", "Missing or invalid Authorization header");
            return ResponseEntity.status(401).body(response);
        }

        String token = authHeader.substring(7);

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

    // ✅ UptimeRobot ping route
    @GetMapping("/")
    public String ping() {
        return "Backend is live!";
    }
}
