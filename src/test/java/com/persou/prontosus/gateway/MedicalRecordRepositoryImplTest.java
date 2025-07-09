package com.persou.prontosus.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import com.persou.prontosus.mocks.MedicalRecordMock;
import com.persou.prontosus.mocks.PatientMock;
import com.persou.prontosus.mocks.UserMock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MedicalRecordRepositoryImplTest {

    @Mock
    private MedicalRecordJpaRepository medicalRecordJpaRepository;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MedicalRecordRepositoryImpl medicalRecordRepository;

    private MedicalRecord medicalRecordDomain;
    private MedicalRecordEntity medicalRecordEntity;
    private Patient patientDomain;
    private PatientEntity patientEntity;
    private User userDomain;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        medicalRecordDomain = MedicalRecordMock.mockDomain();
        medicalRecordEntity = MedicalRecordMock.mockEntity();
        patientDomain = PatientMock.mockDomain();
        patientEntity = PatientMock.mockEntity();
        userDomain = UserMock.mockDomain();
        userEntity = UserMock.mockEntity();
    }

    @Test
    void shouldFindByPatientOrderByConsultationDateDesc() {

        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);
        List<MedicalRecord> domains = List.of(medicalRecordDomain);

        when(patientMapper.toEntity(patientDomain)).thenReturn(patientEntity);
        when(medicalRecordJpaRepository.findByPatientOrderByConsultationDateDesc(patientEntity))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        List<MedicalRecord> result = medicalRecordRepository.findByPatientOrderByConsultationDateDesc(patientDomain);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(patientMapper, times(1)).toEntity(patientDomain);
        verify(medicalRecordJpaRepository, times(1)).findByPatientOrderByConsultationDateDesc(patientEntity);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldThrowExceptionWhenMappingFailsInFindByPatient() {

        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);

        when(patientMapper.toEntity(patientDomain)).thenReturn(patientEntity);
        when(medicalRecordJpaRepository.findByPatientOrderByConsultationDateDesc(patientEntity))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity))
            .thenThrow(new RuntimeException("Mapping error"));

        assertThatThrownBy(() -> medicalRecordRepository.findByPatientOrderByConsultationDateDesc(patientDomain))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Erro no mapeamento do registro médico");

        verify(patientMapper, times(1)).toEntity(patientDomain);
        verify(medicalRecordJpaRepository, times(1)).findByPatientOrderByConsultationDateDesc(patientEntity);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldFindByHealthcareProfessionalOrderByConsultationDateDesc() {

        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);
        List<MedicalRecord> domains = List.of(medicalRecordDomain);

        when(userMapper.toEntity(userDomain)).thenReturn(userEntity);
        when(medicalRecordJpaRepository.findByHealthcareProfessionalOrderByConsultationDateDesc(userEntity))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        List<MedicalRecord> result = medicalRecordRepository
            .findByHealthcareProfessionalOrderByConsultationDateDesc(userDomain);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(userMapper, times(1)).toEntity(userDomain);
        verify(medicalRecordJpaRepository, times(1))
            .findByHealthcareProfessionalOrderByConsultationDateDesc(userEntity);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldFindByPatientIdOrderByConsultationDateDesc() {

        String patientId = "patient-id";
        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);
        List<MedicalRecord> domains = List.of(medicalRecordDomain);

        when(medicalRecordJpaRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        List<MedicalRecord> result = medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(medicalRecordJpaRepository, times(1)).findByPatientIdOrderByConsultationDateDesc(patientId);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldThrowExceptionWhenMappingFailsInFindByPatientId() {

        String patientId = "patient-id";
        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);

        when(medicalRecordJpaRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity))
            .thenThrow(new RuntimeException("Mapping error"));

        assertThatThrownBy(() -> medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Erro no mapeamento do registro médico");

        verify(medicalRecordJpaRepository, times(1)).findByPatientIdOrderByConsultationDateDesc(patientId);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldHandleGeneralExceptionInFindByPatientId() {

        String patientId = "patient-id";

        when(medicalRecordJpaRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> medicalRecordRepository.findByPatientIdOrderByConsultationDateDesc(patientId))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(medicalRecordJpaRepository, times(1)).findByPatientIdOrderByConsultationDateDesc(patientId);
    }

    @Test
    void shouldFindByHealthcareProfessionalIdOrderByConsultationDateDesc() {

        String professionalId = "professional-id";
        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);
        List<MedicalRecord> domains = List.of(medicalRecordDomain);

        when(medicalRecordJpaRepository.findByHealthcareProfessionalIdOrderByConsultationDateDesc(professionalId))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        List<MedicalRecord> result = medicalRecordRepository
            .findByHealthcareProfessionalIdOrderByConsultationDateDesc(professionalId);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(medicalRecordJpaRepository, times(1))
            .findByHealthcareProfessionalIdOrderByConsultationDateDesc(professionalId);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldFindByConsultationDateBetween() {

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);
        List<MedicalRecord> domains = List.of(medicalRecordDomain);

        when(medicalRecordJpaRepository.findByConsultationDateBetween(startDate, endDate))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        List<MedicalRecord> result = medicalRecordRepository.findByConsultationDateBetween(startDate, endDate);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(medicalRecordJpaRepository, times(1)).findByConsultationDateBetween(startDate, endDate);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldFindByPatientAndDateRange() {

        String patientId = "patient-id";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<MedicalRecordEntity> entities = List.of(medicalRecordEntity);
        List<MedicalRecord> domains = List.of(medicalRecordDomain);

        when(medicalRecordJpaRepository.findByPatientAndDateRange(patientId, startDate, endDate))
            .thenReturn(entities);
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        List<MedicalRecord> result = medicalRecordRepository.findByPatientAndDateRange(patientId, startDate, endDate);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(medicalRecordJpaRepository, times(1)).findByPatientAndDateRange(patientId, startDate, endDate);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldFindById() {

        String id = "record-id";

        when(medicalRecordJpaRepository.findById(id)).thenReturn(Optional.of(medicalRecordEntity));
        when(medicalRecordMapper.toDomain(medicalRecordEntity)).thenReturn(medicalRecordDomain);


        Optional<MedicalRecord> result = medicalRecordRepository.findById(id);


        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(medicalRecordDomain);
        verify(medicalRecordJpaRepository, times(1)).findById(id);
        verify(medicalRecordMapper, times(1)).toDomain(medicalRecordEntity);
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {

        String id = "nonexistent-id";

        when(medicalRecordJpaRepository.findById(id)).thenReturn(Optional.empty());


        Optional<MedicalRecord> result = medicalRecordRepository.findById(id);


        assertThat(result).isEmpty();
        verify(medicalRecordJpaRepository, times(1)).findById(id);
        verify(medicalRecordMapper, times(0)).toDomain(any());
    }

}