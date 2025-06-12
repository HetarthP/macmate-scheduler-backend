package com.mcmaster.scheduler.controller;
//import statements
import com.mcmaster.scheduler.model.User;
import com.mcmaster.scheduler.repository.UserRepository;
import com.mcmaster.scheduler.dto.LoginRequest;
import com.mcmaster.scheduler.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public User register(@RequestBody User user) {
        try {
            // 🔐 Encrypt the password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (Exception e) {
            System.out.println(" ERROR IN REGISTRATION:");
            e.printStackTrace();
            return null;
        }
    }
//handling the login requests for new users basically
    @PostMapping("/login")
public String login(@RequestBody LoginRequest request) {
    try {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return "User not found";
        }

        User user = userOpt.get();
//checks if the password matches the one registered and generates the jwt token
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            //Generate JWT token
            return jwtService.generateToken(user.getEmail()); // only return token!
        } else {
            return "Invalid password";
        }

    } catch (Exception e) {
        System.out.println(" ERROR IN LOGIN:");
        e.printStackTrace();
        return "Login failed";
    }
}

    //  Protected endpoint using JWT
    //use checks and crosses for clearer parts of code and better UI for user
    @GetMapping("/protected")
    public String protectedEndpoint(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "❌ Missing or invalid Authorization header";
        }

        String token = authHeader.substring(7); // remove "Bearer "

        try {
            String email = jwtService.extractEmail(token);
            return "✅ Access granted for: " + email;
        } catch (Exception e) {
            return "❌ Invalid or expired token";
        }
    }
}
