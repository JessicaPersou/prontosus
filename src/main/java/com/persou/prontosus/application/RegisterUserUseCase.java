package com.persou.prontosus.application;

import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User execute(User user) {
        validateUser(user);

        // Criar nova instância com senha criptografada e status ativo
        String encodedPassword = passwordEncoder.encode(user.password());

        User userToSave = user
            .withPassword(encodedPassword)
            .withActive(true)
            .withCreatedAt(LocalDateTime.now())
            .withUpdatedAt(LocalDateTime.now());

        return userRepository.save(userToSave);
    }

    private void validateUser(User user) {
        if (userRepository.existsByUsername(user.username())) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        if (userRepository.existsByEmail(user.email())) {
            throw new IllegalArgumentException("Email já está cadastrado");
        }

        if (userRepository.existsByProfessionalDocument(user.professionalDocument())) {
            throw new IllegalArgumentException("Documento profissional já está cadastrado");
        }
    }
}