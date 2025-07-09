package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.gateway.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMedicalRecordUseCaseTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    private CreateMedicalRecordUseCase createMedicalRecordUseCase;

    @BeforeEach
    void setUp() {
        createMedicalRecordUseCase = new CreateMedicalRecordUseCase(
            medicalRecordRepository, patientRepository, userRepository);
    }

    @Test
    void shouldCreateMedicalRecordSuccessfully() {
        String patientId = "patient1";
        String professionalId = "prof1";

        Patient patient = Patient.builder()
            .id(patientId)
            .fullName("Test Patient")
            .build();

        User professional = User.builder()
            .id(professionalId)
            .fullName("Dr. Test")
            .build();

        MedicalRecord medicalRecord = MedicalRecord.builder()
            .chiefComplaint("Test complaint")
            .diagnosis("Test diagnosis")
            .consultationDate(LocalDateTime.now())
            .build();

        MedicalRecord savedRecord = medicalRecord
            .withId("record1")
            .withPatient(patient)
            .withHealthcareProfessional(professional);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        MedicalRecord result = createMedicalRecordUseCase.execute(patientId, professionalId, medicalRecord);

        assertNotNull(result);
        assertEquals("record1", result.id());
        assertEquals(patient, result.patient());
        assertEquals(professional, result.healthcareProfessional());

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(professionalId);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        String patientId = "nonexistent";
        String professionalId = "prof1";

        MedicalRecord medicalRecord = MedicalRecord.builder()
            .chiefComplaint("Test complaint")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> createMedicalRecordUseCase.execute(patientId, professionalId, medicalRecord));

        verify(patientRepository).findById(patientId);
        verify(userRepository, never()).findById(anyString());
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenProfessionalNotFound() {
        String patientId = "patient1";
        String professionalId = "nonexistent";

        Patient patient = Patient.builder()
            .id(patientId)
            .fullName("Test Patient")
            .build();

        MedicalRecord medicalRecord = MedicalRecord.builder()
            .chiefComplaint("Test complaint")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> createMedicalRecordUseCase.execute(patientId, professionalId, medicalRecord));

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(professionalId);
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void shouldSetConsultationDateWhenNull() {
        String patientId = "patient1";
        String professionalId = "prof1";

        Patient patient = Patient.builder().id(patientId).build();
        User professional = User.builder().id(professionalId).build();

        MedicalRecord medicalRecord = MedicalRecord.builder()
            .chiefComplaint("Test complaint")
            .consultationDate(null)
            .build();

        MedicalRecord savedRecord = medicalRecord.withId("record1");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        MedicalRecord result = createMedicalRecordUseCase.execute(patientId, professionalId, medicalRecord);

        assertNotNull(result);
        verify(medicalRecordRepository).save(argThat(record ->
            record.consultationDate() != null));
    }

    @Test
    void shouldPreserveConsultationDateWhenProvided() {
        String patientId = "patient1";
        String professionalId = "prof1";
        LocalDateTime consultationDate = LocalDateTime.now().minusHours(2);

        Patient patient = Patient.builder().id(patientId).build();
        User professional = User.builder().id(professionalId).build();

        MedicalRecord medicalRecord = MedicalRecord.builder()
            .chiefComplaint("Test complaint")
            .consultationDate(consultationDate)
            .build();

        MedicalRecord savedRecord = medicalRecord.withId("record1");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        MedicalRecord result = createMedicalRecordUseCase.execute(patientId, professionalId, medicalRecord);

        assertNotNull(result);
        verify(medicalRecordRepository).save(argThat(record ->
            consultationDate.equals(record.consultationDate())));
    }
}