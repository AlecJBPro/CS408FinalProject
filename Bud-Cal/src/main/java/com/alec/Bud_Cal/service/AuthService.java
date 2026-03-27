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
            return Optional.empty();
        }

        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setHashedPassword(passwordEncoder.encode(form.getPassword()));
        user.setCreatedAt(Instant.now());
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> authenticate(LoginForm form) {
        return userRepository.findByEmail(form.getEmail())
                .filter(user -> passwordEncoder.matches(form.getPassword(), user.getHashedPassword()));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
