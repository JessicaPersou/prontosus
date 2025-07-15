package com.persou.prontosus.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ViewMedicalHistoryUseCaseTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    private ViewMedicalHistoryUseCase viewMedicalHistoryUseCase;

    @BeforeEach
    void setUp() {
        viewMedicalHistoryUseCase = new ViewMedicalHistoryUseCase(medicalRecordRepository);
    }

    @Test
    void shouldGetPatientHistory() {
        String patientId = "patient1";
        LocalDateTime now = LocalDateTime.now();

        List<MedicalRecord> records = List.of(
            MedicalRecord.builder()
                .id("record1")
                .chiefComplaint("Test complaint 1")
                .diagnosis("Test diagnosis 1")
                .consultationDate(now)
                .build(),
            MedicalRecord.builder()
                .id("record2")
                .chiefComplaint("Test complaint 2")
                .diagnosis("Test diagnosis 2")
                .consultationDate(now.minusDays(1))
                .build()
        );

        when(medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .thenReturn(records);

        List<MedicalRecord> result = viewMedicalHistoryUseCase.getPatientHistory(patientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("record1", result.get(0).id());
        assertEquals("record2", result.get(1).id());
        assertEquals("Test complaint 1", result.get(0).chiefComplaint());
        assertEquals("Test complaint 2", result.get(1).chiefComplaint());

        verify(medicalRecordRepository).findByPatientIdOrderByConsultationDateDesc(patientId);
    }

    @Test
    void shouldReturnEmptyListWhenNoRecordsFound() {
        String patientId = "patient1";

        when(medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .thenReturn(List.of());

        List<MedicalRecord> result = viewMedicalHistoryUseCase.getPatientHistory(patientId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(medicalRecordRepository).findByPatientIdOrderByConsultationDateDesc(patientId);
    }

    @Test
    void shouldGetPatientHistoryByDateRange() {
        String patientId = "patient1";
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        List<MedicalRecord> records = List.of(
            MedicalRecord.builder()
                .id("record1")
                .chiefComplaint("Recent complaint")
                .consultationDate(LocalDateTime.now().minusDays(5))
                .build()
        );

        when(medicalRecordRepository.findByPatientAndDateRange(patientId, startDate, endDate))
            .thenReturn(records);

        List<MedicalRecord> result = viewMedicalHistoryUseCase.getPatientHistoryByDateRange(
            patientId, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("record1", result.get(0).id());
        assertEquals("Recent complaint", result.get(0).chiefComplaint());

        verify(medicalRecordRepository).findByPatientAndDateRange(patientId, startDate, endDate);
    }
}