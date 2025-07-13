package com.persou.prontosus.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthenticateUserUseCase authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        authenticateUserUseCase = new AuthenticateUserUseCase(userRepository, passwordEncoder);
    }

    @Test
    void shouldAuthenticateValidUser() {
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "$2a$10$encoded";

        User user = User.builder()
            .id("1")
            .username(username)
            .password(encodedPassword)
            .fullName("Test User")
            .email("test@email.com")
            .active(true)
            .build();

        User updatedUser = user.withLastLoginAt(LocalDateTime.now());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        Optional<User> result = authenticateUserUseCase.execute(username, password);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().username());
        assertNotNull(result.get().lastLoginAt());

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFailAuthenticationWhenUserNotFound() {
        String username = "nonexistent";
        String password = "password123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = authenticateUserUseCase.execute(username, password);

        assertFalse(result.isPresent());

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldFailAuthenticationWhenPasswordDoesNotMatch() {
        String username = "testuser";
        String password = "wrongpassword";
        String encodedPassword = "$2a$10$encoded";

        User user = User.builder()
            .id("1")
            .username(username)
            .password(encodedPassword)
            .active(true)
            .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        Optional<User> result = authenticateUserUseCase.execute(username, password);

        assertFalse(result.isPresent());

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(userRepository, never()).save(any());
    }

}