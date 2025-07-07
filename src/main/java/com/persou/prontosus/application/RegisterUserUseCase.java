package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.DOCUMENT_ALREADY_EXISTS;
import static com.persou.prontosus.config.MessagesErrorException.EMAIL_ALREADY_EXISTS;
import static com.persou.prontosus.config.MessagesErrorException.USER_ALREADY_EXISTS;

import com.persou.prontosus.config.exceptions.ResourceAlreadyExistsException;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.ProfessionalRole;
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

        String encodedPassword = passwordEncoder.encode(user.password());

        ProfessionalRole roleEnum = mapStringToRole(user.role());

        User userToSave = user
            .withPassword(encodedPassword)
            .withRole(roleEnum.name())
            .withActive(true)
            .withCreatedAt(LocalDateTime.now())
            .withUpdatedAt(LocalDateTime.now());

        return userRepository.save(userToSave);
    }

    private void validateUser(User user) {
        if (userRepository.existsByUsername(user.username())) {
            throw new ResourceAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(user.email())) {
            throw new ResourceAlreadyExistsException(EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByProfessionalDocument(user.professionalDocument())) {
            throw new ResourceAlreadyExistsException(DOCUMENT_ALREADY_EXISTS);
        }
    }

    private ProfessionalRole mapStringToRole(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role é obrigatório");
        }
        try {
            return ProfessionalRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role inválido: " + role + ". Valores aceitos: DOCTOR, NURSE, ADMIN");
        }
    }
}