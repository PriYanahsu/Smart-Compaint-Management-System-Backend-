package com.example.demo.controller;

import com.example.demo.entity.AuthEntity;
import com.example.demo.repository.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthControl {

    @Autowired
    private PasswordEncoder passwordencoder;

    @Autowired
    private AuthRepo repo;

    @PostMapping("/register")
    public ResponseEntity<?> registration(@RequestBody AuthEntity user) {
        user.setPassword(passwordencoder.encode(user.getPassword()));
        repo.save(user);
        if (Objects.equals(user.getRole(), "user")) {
            return ResponseEntity.ok("Registration Successful for user");
        }
        return ResponseEntity.ok("Registration Successful for admin");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthEntity user) {
        Optional<AuthEntity> allUser = repo.findByUsername(user.getUsername());

        // Check if user exists
        if (allUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid Username or Password");
        }

        AuthEntity existingUser = allUser.get();
        boolean passwordMatch = passwordencoder.matches(user.getPassword(), existingUser.getPassword());
        boolean roleMatch = Objects.equals(user.getRole(), existingUser.getRole());

        if (passwordMatch && roleMatch) {
            if (Objects.equals(user.getRole(), "admin")) {
                return ResponseEntity.ok("Admin Login SuccessFull");
            } else {
                return ResponseEntity.ok("User Login SuccessFull");
            }
        }

        return ResponseEntity.status(401).body("Invalid Username or Password");
    }
}
