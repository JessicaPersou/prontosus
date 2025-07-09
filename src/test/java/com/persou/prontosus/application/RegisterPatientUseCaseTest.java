package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceAlreadyExistsException;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterPatientUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    private RegisterPatientUseCase registerPatientUseCase;

    @BeforeEach
    void setUp() {
        registerPatientUseCase = new RegisterPatientUseCase(patientRepository);
    }

    @Test
    void shouldRegisterPatientSuccessfully() {
        Patient patient = Patient.builder()
            .cpf("12345678901")
            .fullName("Test Patient")
            .birthDate(LocalDate.of(1990, 1, 1))
            .gender("MALE")
            .email("test@email.com")
            .phoneNumber("11987654321")
            .build();

        Patient savedPatient = patient.withId("patient1");

        when(patientRepository.existsByCpf(patient.cpf())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(savedPatient);

        Patient result = registerPatientUseCase.execute(patient);

        assertNotNull(result);
        assertEquals("patient1", result.id());
        assertEquals(patient.cpf(), result.cpf());
        assertEquals(patient.fullName(), result.fullName());

        verify(patientRepository).existsByCpf(patient.cpf());
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        Patient patient = Patient.builder()
            .cpf("12345678901")
            .fullName("Test Patient")
            .birthDate(LocalDate.of(1990, 1, 1))
            .gender("MALE")
            .build();

        when(patientRepository.existsByCpf(patient.cpf())).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
            () -> registerPatientUseCase.execute(patient));

        assertNotNull(exception.getMessage());

        verify(patientRepository).existsByCpf(patient.cpf());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldValidatePatientDataBeforeRegistration() {
        Patient patient = Patient.builder()
            .cpf("98765432100")
            .fullName("Another Patient")
            .birthDate(LocalDate.of(1985, 5, 15))
            .gender("FEMALE")
            .email("another@email.com")
            .build();

        Patient savedPatient = patient.withId("patient2");

        when(patientRepository.existsByCpf(patient.cpf())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(savedPatient);

        Patient result = registerPatientUseCase.execute(patient);

        assertNotNull(result);
        assertEquals("patient2", result.id());
        assertEquals("98765432100", result.cpf());
        assertEquals("Another Patient", result.fullName());
        assertEquals("FEMALE", result.gender());

        verify(patientRepository).existsByCpf("98765432100");
        verify(patientRepository).save(patient);
    }

    @Test
    void shouldHandlePatientWithMinimalData() {
        Patient patient = Patient.builder()
            .cpf("11111111111")
            .fullName("Minimal Patient")
            .birthDate(LocalDate.of(2000, 12, 31))
            .gender("OTHER")
            .build();

        Patient savedPatient = patient.withId("patient3");

        when(patientRepository.existsByCpf(patient.cpf())).thenReturn(false);
        when(patientRepository.save(patient)).thenReturn(savedPatient);

        Patient result = registerPatientUseCase.execute(patient);

        assertNotNull(result);
        assertEquals("patient3", result.id());
        assertEquals("11111111111", result.cpf());
        assertEquals("Minimal Patient", result.fullName());
        assertNull(result.email());
        assertNull(result.phoneNumber());

        verify(patientRepository).existsByCpf(patient.cpf());
        verify(patientRepository).save(patient);
    }
}