package com.persou.prontosus.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import com.persou.prontosus.gateway.database.jpa.repository.PatientJpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatientRepositoryImplTest {

    @Mock
    private PatientJpaRepository patientJpaRepository;

    @Mock
    private PatientMapper patientMapper;

    private PatientRepositoryImpl patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository = new PatientRepositoryImpl(patientJpaRepository, patientMapper);
    }

    @Test
    void shouldSavePatient() {
        Patient patient = Patient.builder()
            .id("patient1")
            .cpf("12345678901")
            .fullName("Test Patient")
            .birthDate(LocalDate.of(1990, 1, 1))
            .gender("MALE")
            .email("test@email.com")
            .createdAt(LocalDateTime.now())
            .build();

        PatientEntity entity = PatientEntity.builder()
            .id("patient1")
            .cpf("12345678901")
            .fullName("Test Patient")
            .birthDate(LocalDate.of(1990, 1, 1))
            .email("test@email.com")
            .createdAt(patient.createdAt())
            .build();

        PatientEntity savedEntity = entity.toBuilder().build();

        when(patientMapper.toEntity(patient)).thenReturn(entity);
        when(patientJpaRepository.save(entity)).thenReturn(savedEntity);
        when(patientMapper.toDomain(savedEntity)).thenReturn(patient);

        Patient result = patientRepository.save(patient);

        assertNotNull(result);
        assertEquals(patient.id(), result.id());
        assertEquals(patient.cpf(), result.cpf());
        assertEquals(patient.fullName(), result.fullName());

        verify(patientMapper).toEntity(patient);
        verify(patientJpaRepository).save(entity);
        verify(patientMapper).toDomain(savedEntity);
    }

    @Test
    void shouldThrowExceptionWhenSavingNullPatient() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> patientRepository.save(null));

        assertNotNull(exception.getMessage());

        verify(patientMapper, never()).toEntity(any());
        verify(patientJpaRepository, never()).save(any());
    }

    @Test
    void shouldFindPatientById() {
        String patientId = "patient1";

        PatientEntity entity = PatientEntity.builder()
            .id(patientId)
            .cpf("12345678901")
            .fullName("Test Patient")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

        Patient patient = Patient.builder()
            .id(patientId)
            .cpf("12345678901")
            .fullName("Test Patient")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();

        when(patientJpaRepository.findById(patientId)).thenReturn(Optional.of(entity));
        when(patientMapper.toDomain(entity)).thenReturn(patient);

        Optional<Patient> result = patientRepository.findById(patientId);

        assertTrue(result.isPresent());
        assertEquals(patientId, result.get().id());
        assertEquals("Test Patient", result.get().fullName());

        verify(patientJpaRepository).findById(patientId);
        verify(patientMapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenPatientNotFoundById() {
        String patientId = "nonexistent";

        when(patientJpaRepository.findById(patientId)).thenReturn(Optional.empty());

        Optional<Patient> result = patientRepository.findById(patientId);

        assertFalse(result.isPresent());

        verify(patientJpaRepository).findById(patientId);
        verify(patientMapper, never()).toDomain(any());
    }

    @Test
    void shouldThrowExceptionWhenFindingByNullId() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> patientRepository.findById(null));

        assertNotNull(exception.getMessage());

        verify(patientJpaRepository, never()).findById(any());
    }

    @Test
    void shouldFindPatientByCpf() {
        String cpf = "12345678901";

        PatientEntity entity = PatientEntity.builder()
            .id("patient1")
            .cpf(cpf)
            .fullName("Test Patient")
            .build();

        Patient patient = Patient.builder()
            .id("patient1")
            .cpf(cpf)
            .fullName("Test Patient")
            .build();

        when(patientJpaRepository.findByCpf(cpf)).thenReturn(Optional.of(entity));
        when(patientMapper.toDomain(entity)).thenReturn(patient);

        Optional<Patient> result = patientRepository.findByCpf(cpf);

        assertTrue(result.isPresent());
        assertEquals(cpf, result.get().cpf());

        verify(patientJpaRepository).findByCpf(cpf);
        verify(patientMapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenPatientNotFoundByCpf() {
        String cpf = "99999999999";

        when(patientJpaRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        Optional<Patient> result = patientRepository.findByCpf(cpf);

        assertFalse(result.isPresent());

        verify(patientJpaRepository).findByCpf(cpf);
        verify(patientMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindPatientsByFullNameContainingIgnoreCase() {
        String name = "Test";

        List<PatientEntity> entities = List.of(
            PatientEntity.builder()
                .id("patient1")
                .fullName("Test Patient One")
                .cpf("11111111111")
                .build(),
            PatientEntity.builder()
                .id("patient2")
                .fullName("Another Test Patient")
                .cpf("22222222222")
                .build()
        );

        List<Patient> patients = List.of(
            Patient.builder().id("patient1").fullName("Test Patient One").build(),
            Patient.builder().id("patient2").fullName("Another Test Patient").build()
        );

        when(patientJpaRepository.findByFullNameContainingIgnoreCase(name)).thenReturn(entities);
        when(patientMapper.toDomain(entities.get(0))).thenReturn(patients.get(0));
        when(patientMapper.toDomain(entities.get(1))).thenReturn(patients.get(1));

        List<Patient> result = patientRepository.findByFullNameContainingIgnoreCase(name);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).fullName().contains("Test"));
        assertTrue(result.get(1).fullName().contains("Test"));

        verify(patientJpaRepository).findByFullNameContainingIgnoreCase(name);
        verify(patientMapper, times(2)).toDomain(any(PatientEntity.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoPatientFoundByName() {
        String name = "NonExistent";

        when(patientJpaRepository.findByFullNameContainingIgnoreCase(name)).thenReturn(List.of());

        List<Patient> result = patientRepository.findByFullNameContainingIgnoreCase(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientJpaRepository).findByFullNameContainingIgnoreCase(name);
        verify(patientMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindAllPatients() {
        List<PatientEntity> entities = List.of(
            PatientEntity.builder()
                .id("patient1")
                .fullName("Patient One")
                .cpf("11111111111")
                .build(),
            PatientEntity.builder()
                .id("patient2")
                .fullName("Patient Two")
                .cpf("22222222222")
                .build(),
            PatientEntity.builder()
                .id("patient3")
                .fullName("Patient Three")
                .cpf("33333333333")
                .build()
        );

        List<Patient> patients = List.of(
            Patient.builder().id("patient1").fullName("Patient One").build(),
            Patient.builder().id("patient2").fullName("Patient Two").build(),
            Patient.builder().id("patient3").fullName("Patient Three").build()
        );

        when(patientJpaRepository.findAll()).thenReturn(entities);
        when(patientMapper.toDomain(entities.get(0))).thenReturn(patients.get(0));
        when(patientMapper.toDomain(entities.get(1))).thenReturn(patients.get(1));
        when(patientMapper.toDomain(entities.get(2))).thenReturn(patients.get(2));

        List<Patient> result = patientRepository.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(patientJpaRepository).findAll();
        verify(patientMapper, times(3)).toDomain(any(PatientEntity.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoPatients() {
        when(patientJpaRepository.findAll()).thenReturn(List.of());

        List<Patient> result = patientRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientJpaRepository).findAll();
        verify(patientMapper, never()).toDomain(any());
    }

    @Test
    void shouldCheckIfPatientExistsByCpf() {
        String cpf = "12345678901";

        when(patientJpaRepository.existsByCpf(cpf)).thenReturn(true);

        boolean result = patientRepository.existsByCpf(cpf);

        assertTrue(result);

        verify(patientJpaRepository).existsByCpf(cpf);
    }

    @Test
    void shouldReturnFalseWhenPatientDoesNotExistByCpf() {
        String cpf = "99999999999";

        when(patientJpaRepository.existsByCpf(cpf)).thenReturn(false);

        boolean result = patientRepository.existsByCpf(cpf);

        assertFalse(result);

        verify(patientJpaRepository).existsByCpf(cpf);
    }

    @Test
    void shouldFindPatientsByPhoneNumber() {
        String phoneNumber = "11987654321";

        List<PatientEntity> entities = List.of(
            PatientEntity.builder()
                .id("patient1")
                .fullName("Patient with Phone")
                .phoneNumber(phoneNumber)
                .build()
        );

        List<Patient> patients = List.of(
            Patient.builder().id("patient1").fullName("Patient with Phone").phoneNumber(phoneNumber).build()
        );

        when(patientJpaRepository.findByPhoneNumber(phoneNumber)).thenReturn(entities);
        when(patientMapper.toDomain(entities.get(0))).thenReturn(patients.get(0));

        List<Patient> result = patientRepository.findByPhoneNumber(phoneNumber);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(phoneNumber, result.get(0).phoneNumber());

        verify(patientJpaRepository).findByPhoneNumber(phoneNumber);
        verify(patientMapper).toDomain(entities.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoPatientFoundByPhone() {
        String phoneNumber = "99999999999";

        when(patientJpaRepository.findByPhoneNumber(phoneNumber)).thenReturn(List.of());

        List<Patient> result = patientRepository.findByPhoneNumber(phoneNumber);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientJpaRepository).findByPhoneNumber(phoneNumber);
        verify(patientMapper, never()).toDomain(any());
    }
}