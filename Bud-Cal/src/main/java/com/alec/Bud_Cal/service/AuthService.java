package com.alec.Bud_Cal.service;

import com.alec.Bud_Cal.model.LoginForm;
import com.alec.Bud_Cal.model.SignupForm;
import com.alec.Bud_Cal.model.User;
import java.time.Instant;
import com.alec.Bud_Cal.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> register(SignupForm form) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            System.out.println("User already exists: " + form.getEmail());
            return Optional.empty();
        }

        System.out.println("Creating new user: " + form.getEmail());
        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setHashedPassword(passwordEncoder.encode(form.getPassword()));
        user.setCreatedAt(Instant.now());
        try {
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully: " + savedUser.getEmail());
            return Optional.of(savedUser);
        } catch (Exception e) {
            System.out.println("Error saving user: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> authenticate(LoginForm form) {
        System.out.println("Authenticating user: " + form.getEmail());
        Optional<User> user = userRepository.findByEmail(form.getEmail());
        if (user.isPresent()) {
            System.out.println("User found: " + form.getEmail());
            boolean passwordMatches = passwordEncoder.matches(form.getPassword(), user.get().getHashedPassword());
            System.out.println("Password matches: " + passwordMatches);
            return passwordMatches ? user : Optional.empty();
        } else {
            System.out.println("User not found: " + form.getEmail());
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
