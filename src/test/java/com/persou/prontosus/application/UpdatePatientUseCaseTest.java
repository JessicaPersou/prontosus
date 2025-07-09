package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.valueobject.Address;
import com.persou.prontosus.gateway.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePatientUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    private UpdatePatientUseCase updatePatientUseCase;

    @BeforeEach
    void setUp() {
        updatePatientUseCase = new UpdatePatientUseCase(patientRepository);
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        String patientId = "nonexistent";

        Patient updatedPatient = Patient.builder()
            .fullName("New Name")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> updatePatientUseCase.execute(patientId, updatedPatient));

        assertNotNull(exception.getMessage());

        verify(patientRepository).findById(patientId);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldPreserveExistingMedicalRecordsAndAppointments() {
        String patientId = "patient1";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Patient existingPatient = Patient.builder()
            .id(patientId)
            .cpf("12345678901")
            .fullName("Test Patient")
            .createdAt(createdAt)
            .medicalRecords(java.util.List.of())
            .appointments(java.util.List.of())
            .build();

        Patient updatedPatient = Patient.builder()
            .cpf("12345678901")
            .fullName("Updated Patient")
            .build();

        Patient savedPatient = updatedPatient.withId(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = updatePatientUseCase.execute(patientId, updatedPatient);

        assertNotNull(result);
        verify(patientRepository).save(argThat(patient ->
            patient.medicalRecords() != null && patient.appointments() != null));
    }

    @Test
    void shouldUpdatePatientWithAddress() {
        String patientId = "patient1";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Address newAddress = Address.builder()
            .zipCode("01310100")
            .street("Avenida Paulista")
            .number("1000")
            .city("São Paulo")
            .state("SP")
            .build();

        Patient existingPatient = Patient.builder()
            .id(patientId)
            .cpf("12345678901")
            .fullName("Test Patient")
            .createdAt(createdAt)
            .build();

        Patient updatedPatient = Patient.builder()
            .cpf("12345678901")
            .fullName("Test Patient")
            .address(newAddress)
            .build();

        Patient savedPatient = updatedPatient.withId(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = updatePatientUseCase.execute(patientId, updatedPatient);

        assertNotNull(result);
        assertNotNull(result.address());
        assertEquals("01310100", result.address().zipCode());
        assertEquals("Avenida Paulista", result.address().street());
        assertEquals("São Paulo", result.address().city());

        verify(patientRepository).save(argThat(patient ->
            patient.address() != null &&
                "Avenida Paulista".equals(patient.address().street())));
    }

    @Test
    void shouldUpdateEmergencyContactInformation() {
        String patientId = "patient1";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Patient existingPatient = Patient.builder()
            .id(patientId)
            .cpf("12345678901")
            .fullName("Test Patient")
            .createdAt(createdAt)
            .emergencyContactName("Old Contact")
            .emergencyContactPhone("11111111111")
            .build();

        Patient updatedPatient = Patient.builder()
            .cpf("12345678901")
            .fullName("Test Patient")
            .emergencyContactName("New Contact")
            .emergencyContactPhone("22222222222")
            .build();

        Patient savedPatient = updatedPatient.withId(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = updatePatientUseCase.execute(patientId, updatedPatient);

        assertNotNull(result);
        assertEquals("New Contact", result.emergencyContactName());
        assertEquals("22222222222", result.emergencyContactPhone());

        verify(patientRepository).save(argThat(patient ->
            "New Contact".equals(patient.emergencyContactName()) &&
                "22222222222".equals(patient.emergencyContactPhone())));
    }

    @Test
    void shouldUpdateMedicalInformation() {
        String patientId = "patient1";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        Patient existingPatient = Patient.builder()
            .id(patientId)
            .cpf("12345678901")
            .fullName("Test Patient")
            .createdAt(createdAt)
            .knownAllergies("Nenhuma")
            .currentMedications("Nenhuma")
            .chronicConditions("Nenhuma")
            .build();

        Patient updatedPatient = Patient.builder()
            .cpf("12345678901")
            .fullName("Test Patient")
            .knownAllergies("Penicilina")
            .currentMedications("Losartana 50mg")
            .chronicConditions("Hipertensão")
            .build();

        Patient savedPatient = updatedPatient.withId(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        Patient result = updatePatientUseCase.execute(patientId, updatedPatient);

        assertNotNull(result);
        assertEquals("Penicilina", result.knownAllergies());
        assertEquals("Losartana 50mg", result.currentMedications());
        assertEquals("Hipertensão", result.chronicConditions());
    }
}