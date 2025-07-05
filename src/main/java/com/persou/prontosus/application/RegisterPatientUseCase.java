package com.persou.prontosus.application;

import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterPatientUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> execute(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.active() && passwordEncoder.matches(password, user.password())) {
                user.lastLoginAt(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
