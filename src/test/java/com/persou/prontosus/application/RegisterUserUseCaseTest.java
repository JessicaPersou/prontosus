package com.persou.prontosus.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.config.exceptions.ResourceAlreadyExistsException;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        registerUserUseCase = new RegisterUserUseCase(userRepository, passwordEncoder);
    }


    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        User user = User.builder()
            .username("existinguser")
            .password("password123")
            .email("test@email.com")
            .professionalDocument("CRM123456")
            .role("DOCTOR")
            .build();

        when(userRepository.existsByUsername(user.username())).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
            () -> registerUserUseCase.execute(user));

        assertNotNull(exception.getMessage());

        verify(userRepository).existsByUsername(user.username());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        User user = User.builder()
            .username("testuser")
            .password("password123")
            .email("existing@email.com")
            .professionalDocument("CRM123456")
            .role("DOCTOR")
            .build();

        when(userRepository.existsByUsername(user.username())).thenReturn(false);
        when(userRepository.existsByEmail(user.email())).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
            () -> registerUserUseCase.execute(user));

        assertNotNull(exception.getMessage());

        verify(userRepository).existsByUsername(user.username());
        verify(userRepository).existsByEmail(user.email());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowExceptionWhenProfessionalDocumentAlreadyExists() {
        User user = User.builder()
            .username("testuser")
            .password("password123")
            .email("test@email.com")
            .professionalDocument("CRM123456")
            .role("DOCTOR")
            .build();

        when(userRepository.existsByUsername(user.username())).thenReturn(false);
        when(userRepository.existsByEmail(user.email())).thenReturn(false);
        when(userRepository.existsByProfessionalDocument(user.professionalDocument())).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
            () -> registerUserUseCase.execute(user));

        assertNotNull(exception.getMessage());

        verify(userRepository).existsByUsername(user.username());
        verify(userRepository).existsByEmail(user.email());
        verify(userRepository).existsByProfessionalDocument(user.professionalDocument());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowExceptionForNullRole() {
        User user = User.builder()
            .username("testuser")
            .password("password123")
            .email("test@email.com")
            .professionalDocument("CRM123456")
            .role(null)
            .build();

        when(userRepository.existsByUsername(user.username())).thenReturn(false);
        when(userRepository.existsByEmail(user.email())).thenReturn(false);
        when(userRepository.existsByProfessionalDocument(user.professionalDocument())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> registerUserUseCase.execute(user));

        assertEquals("Role é obrigatório", exception.getMessage());
    }

    @Test
    void shouldRegisterNurseSuccessfully() {
        User user = User.builder()
            .username("nurse1")
            .password("password123")
            .fullName("Nurse Test")
            .email("nurse@email.com")
            .professionalDocument("COREN78901")
            .role("NURSE")
            .specialty("Enfermagem Geral")
            .build();

        String encodedPassword = "$2a$10$encoded";
        User savedUser = user.withId("user2").withPassword(encodedPassword).withRole("NURSE").withActive(true);

        when(userRepository.existsByUsername(user.username())).thenReturn(false);
        when(userRepository.existsByEmail(user.email())).thenReturn(false);
        when(userRepository.existsByProfessionalDocument(user.professionalDocument())).thenReturn(false);
        when(passwordEncoder.encode(user.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = registerUserUseCase.execute(user);

        assertNotNull(result);
        assertEquals("NURSE", result.role());
        assertEquals("Enfermagem Geral", result.specialty());
    }

    @Test
    void shouldRegisterAdminSuccessfully() {
        User user = User.builder()
            .username("admin1")
            .password("password123")
            .fullName("Admin Test")
            .email("admin@email.com")
            .professionalDocument("ADM123456")
            .role("ADMIN")
            .build();

        String encodedPassword = "$2a$10$encoded";
        User savedUser = user.withId("user3").withPassword(encodedPassword).withRole("ADMIN").withActive(true);

        when(userRepository.existsByUsername(user.username())).thenReturn(false);
        when(userRepository.existsByEmail(user.email())).thenReturn(false);
        when(userRepository.existsByProfessionalDocument(user.professionalDocument())).thenReturn(false);
        when(passwordEncoder.encode(user.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = registerUserUseCase.execute(user);

        assertNotNull(result);
        assertEquals("ADMIN", result.role());
        assertNull(result.specialty());
    }
}