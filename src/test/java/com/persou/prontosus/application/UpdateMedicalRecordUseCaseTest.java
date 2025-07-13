package com.persou.prontosus.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateMedicalRecordUseCaseTest {

    @Mock
    private MedicalRecordJpaRepository medicalRecordJpaRepository;

    private UpdateMedicalRecordUseCase updateMedicalRecordUseCase;

    @BeforeEach
    void setUp() {
        updateMedicalRecordUseCase = new UpdateMedicalRecordUseCase(medicalRecordJpaRepository);
    }

    @Test
    void shouldUpdateMedicalRecordSuccessfully() {
        String recordId = "record1";
        LocalDateTime consultationDate = LocalDateTime.now().minusDays(1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(consultationDate)
            .chiefComplaint("Original complaint")
            .diagnosis("Original diagnosis")
            .treatment("Original treatment")
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .chiefComplaint("Updated complaint")
            .diagnosis("Updated diagnosis")
            .treatment("Updated treatment")
            .prescriptions("New prescriptions")
            .observations("New observations")
            .build();

        MedicalRecordEntity savedEntity = existingEntity.toBuilder()
            .chiefComplaint("Updated complaint")
            .diagnosis("Updated diagnosis")
            .treatment("Updated treatment")
            .prescriptions("New prescriptions")
            .observations("New observations")
            .updatedAt(LocalDateTime.now())
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenReturn(savedEntity);

        MedicalRecord result = updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        assertNotNull(result);
        assertEquals(recordId, result.id());
        assertEquals("Updated complaint", result.chiefComplaint());
        assertEquals("Updated diagnosis", result.diagnosis());
        assertEquals("Updated treatment", result.treatment());
        assertEquals("New prescriptions", result.prescriptions());
        assertEquals("New observations", result.observations());
        assertEquals(consultationDate, result.consultationDate());

        verify(medicalRecordJpaRepository).findById(recordId);
        verify(medicalRecordJpaRepository).save(any(MedicalRecordEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenMedicalRecordNotFound() {
        String recordId = "nonexistent";

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .chiefComplaint("Updated complaint")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> updateMedicalRecordUseCase.execute(recordId, updatedRecord));

        assertNotNull(exception.getMessage());

        verify(medicalRecordJpaRepository).findById(recordId);
        verify(medicalRecordJpaRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        String recordId = "record1";
        LocalDateTime consultationDate = LocalDateTime.now().minusDays(1);

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(consultationDate)
            .chiefComplaint("Original complaint")
            .diagnosis("Original diagnosis")
            .treatment("Original treatment")
            .prescriptions("Original prescriptions")
            .observations("Original observations")
            .createdAt(LocalDateTime.now().minusDays(2))
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .diagnosis("Updated diagnosis only")
            .build();

        MedicalRecordEntity savedEntity = existingEntity.toBuilder()
            .diagnosis("Updated diagnosis only")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenReturn(savedEntity);

        MedicalRecord result = updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        assertNotNull(result);
        assertEquals("Updated diagnosis only", result.diagnosis());

        verify(medicalRecordJpaRepository).save(argThat(entity ->
            "Updated diagnosis only".equals(entity.getDiagnosis()) &&
                "Original complaint".equals(entity.getChiefComplaint()) &&
                "Original treatment".equals(entity.getTreatment()) &&
                "Original prescriptions".equals(entity.getPrescriptions()) &&
                "Original observations".equals(entity.getObservations())));
    }

    @Test
    void shouldSetConsultationDateWhenNull() {
        String recordId = "record1";

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(null)
            .chiefComplaint("Original complaint")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .diagnosis("Updated diagnosis")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        verify(medicalRecordJpaRepository).save(argThat(entity ->
            entity.getConsultationDate() != null));
    }

    @Test
    void shouldSetCreatedAtWhenNull() {
        String recordId = "record1";

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Original complaint")
            .createdAt(null)
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .diagnosis("Updated diagnosis")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        verify(medicalRecordJpaRepository).save(argThat(entity ->
            entity.getCreatedAt() != null));
    }

    @Test
    void shouldUpdateHistoryOfPresentIllness() {
        String recordId = "record1";

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Test complaint")
            .historyOfPresentIllness("Original history")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .historyOfPresentIllness("Updated history of present illness")
            .build();

        MedicalRecordEntity savedEntity = existingEntity.toBuilder()
            .historyOfPresentIllness("Updated history of present illness")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenReturn(savedEntity);

        MedicalRecord result = updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        assertNotNull(result);
        assertEquals("Updated history of present illness", result.historyOfPresentIllness());

        verify(medicalRecordJpaRepository).save(argThat(entity ->
            "Updated history of present illness".equals(entity.getHistoryOfPresentIllness())));
    }

    @Test
    void shouldUpdatePhysicalExamination() {
        String recordId = "record1";

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Test complaint")
            .physicalExamination("Original examination")
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .physicalExamination("Updated physical examination findings")
            .build();

        MedicalRecordEntity savedEntity = existingEntity.toBuilder()
            .physicalExamination("Updated physical examination findings")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenReturn(savedEntity);

        MedicalRecord result = updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        assertNotNull(result);
        assertEquals("Updated physical examination findings", result.physicalExamination());

        verify(medicalRecordJpaRepository).save(argThat(entity ->
            "Updated physical examination findings".equals(entity.getPhysicalExamination())));
    }

    @Test
    void shouldUpdateUpdatedAtTimestamp() {
        String recordId = "record1";
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusHours(1);

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Test complaint")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(originalUpdatedAt)
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .diagnosis("Updated diagnosis")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenAnswer(
            invocation -> invocation.getArgument(0));

        updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        verify(medicalRecordJpaRepository).save(argThat(entity ->
            entity.getUpdatedAt().isAfter(originalUpdatedAt)));
    }

    @Test
    void shouldHandleAllFieldsUpdate() {
        String recordId = "record1";
        LocalDateTime consultationDate = LocalDateTime.now().minusDays(1);

        MedicalRecordEntity existingEntity = MedicalRecordEntity.builder()
            .id(recordId)
            .consultationDate(consultationDate)
            .chiefComplaint("Original complaint")
            .historyOfPresentIllness("Original history")
            .physicalExamination("Original examination")
            .diagnosis("Original diagnosis")
            .treatment("Original treatment")
            .prescriptions("Original prescriptions")
            .observations("Original observations")
            .createdAt(LocalDateTime.now().minusDays(2))
            .build();

        MedicalRecord updatedRecord = MedicalRecord.builder()
            .chiefComplaint("New complaint")
            .historyOfPresentIllness("New history")
            .physicalExamination("New examination")
            .diagnosis("New diagnosis")
            .treatment("New treatment")
            .prescriptions("New prescriptions")
            .observations("New observations")
            .build();

        MedicalRecordEntity savedEntity = existingEntity.toBuilder()
            .chiefComplaint("New complaint")
            .historyOfPresentIllness("New history")
            .physicalExamination("New examination")
            .diagnosis("New diagnosis")
            .treatment("New treatment")
            .prescriptions("New prescriptions")
            .observations("New observations")
            .build();

        when(medicalRecordJpaRepository.findById(recordId)).thenReturn(Optional.of(existingEntity));
        when(medicalRecordJpaRepository.save(any(MedicalRecordEntity.class))).thenReturn(savedEntity);

        MedicalRecord result = updateMedicalRecordUseCase.execute(recordId, updatedRecord);

        assertNotNull(result);
        assertEquals("New complaint", result.chiefComplaint());
        assertEquals("New history", result.historyOfPresentIllness());
        assertEquals("New examination", result.physicalExamination());
        assertEquals("New diagnosis", result.diagnosis());
        assertEquals("New treatment", result.treatment());
        assertEquals("New prescriptions", result.prescriptions());
        assertEquals("New observations", result.observations());
    }
}