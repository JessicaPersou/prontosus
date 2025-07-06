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
@Transactional
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> execute(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Verificar se o usuário está ativo e a senha confere
            if (user.active() && passwordEncoder.matches(password, user.password())) {

                // Como User é um record imutável, criar nova instância com lastLoginAt atualizado
                User updatedUser = user.withLastLoginAt(LocalDateTime.now());

                // Salvar o usuário atualizado
                User savedUser = userRepository.save(updatedUser);

                return Optional.of(savedUser);
            }
        }

        return Optional.empty();
    }
}