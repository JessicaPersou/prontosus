package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.AppointmentMapper;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.gateway.database.jpa.AppointmentEntity;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import com.persou.prontosus.gateway.database.jpa.repository.AppointmentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentRepositoryImplTest {

    @Mock
    private AppointmentJpaRepository appointmentJpaRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private UserMapper userMapper;

    private AppointmentRepositoryImpl appointmentRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository = new AppointmentRepositoryImpl(
            appointmentJpaRepository, appointmentMapper, patientMapper, userMapper);
    }

    @Test
    void shouldSaveAppointment() {
        Appointment appointment = Appointment.builder()
            .id("app1")
            .scheduledDateTime(LocalDateTime.now())
            .status("SCHEDULED")
            .build();

        AppointmentEntity entity = AppointmentEntity.builder()
            .id("app1")
            .scheduledDateTime(appointment.scheduledDateTime())
            .status(AppointmentStatus.SCHEDULED)
            .build();

        AppointmentEntity savedEntity = entity.toBuilder().build();

        when(appointmentMapper.toEntity(appointment)).thenReturn(entity);
        when(appointmentJpaRepository.save(entity)).thenReturn(savedEntity);
        when(appointmentMapper.toDomain(savedEntity)).thenReturn(appointment);

        Appointment result = appointmentRepository.save(appointment);

        assertNotNull(result);
        assertEquals(appointment.id(), result.id());

        verify(appointmentMapper).toEntity(appointment);
        verify(appointmentJpaRepository).save(entity);
        verify(appointmentMapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindAppointmentById() {
        String appointmentId = "app1";

        AppointmentEntity entity = AppointmentEntity.builder()
            .id(appointmentId)
            .scheduledDateTime(LocalDateTime.now())
            .build();

        Appointment appointment = Appointment.builder()
            .id(appointmentId)
            .scheduledDateTime(entity.getScheduledDateTime())
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(entity));
        when(appointmentMapper.toDomain(entity)).thenReturn(appointment);

        Optional<Appointment> result = appointmentRepository.findById(appointmentId);

        assertTrue(result.isPresent());
        assertEquals(appointmentId, result.get().id());

        verify(appointmentJpaRepository).findById(appointmentId);
        verify(appointmentMapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenAppointmentNotFound() {
        String appointmentId = "nonexistent";

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Optional<Appointment> result = appointmentRepository.findById(appointmentId);

        assertFalse(result.isPresent());

        verify(appointmentJpaRepository).findById(appointmentId);
        verify(appointmentMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindAppointmentsByPatientOrderByScheduledDateTime() {
        Patient patient = Patient.builder()
            .id("patient1")
            .fullName("Test Patient")
            .build();

        PatientEntity patientEntity = PatientEntity.builder()
            .id("patient1")
            .fullName("Test Patient")
            .build();

        List<AppointmentEntity> entities = List.of(
            AppointmentEntity.builder()
                .id("app1")
                .patient(patientEntity)
                .scheduledDateTime(LocalDateTime.now())
                .build(),
            AppointmentEntity.builder()
                .id("app2")
                .patient(patientEntity)
                .scheduledDateTime(LocalDateTime.now().plusDays(1))
                .build()
        );

        List<Appointment> appointments = List.of(
            Appointment.builder().id("app1").build(),
            Appointment.builder().id("app2").build()
        );

        when(patientMapper.toEntity(patient)).thenReturn(patientEntity);
        when(appointmentJpaRepository.findByPatientOrderByScheduledDateTimeDesc(patientEntity))
            .thenReturn(entities);
        when(appointmentMapper.toDomain(entities.get(0))).thenReturn(appointments.get(0));
        when(appointmentMapper.toDomain(entities.get(1))).thenReturn(appointments.get(1));

        List<Appointment> result = appointmentRepository.findByPatientOrderByScheduledDateTimeDesc(patient);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(patientMapper).toEntity(patient);
        verify(appointmentJpaRepository).findByPatientOrderByScheduledDateTimeDesc(patientEntity);
        verify(appointmentMapper, times(2)).toDomain(any(AppointmentEntity.class));
    }

    @Test
    void shouldFindAppointmentsByProfessionalAndDateRange() {
        User professional = User.builder()
            .id("prof1")
            .fullName("Dr. Test")
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id("prof1")
            .fullName("Dr. Test")
            .build();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        List<AppointmentEntity> entities = List.of(
            AppointmentEntity.builder()
                .id("app1")
                .healthcareProfessional(userEntity)
                .scheduledDateTime(start.plusDays(1))
                .build()
        );

        List<Appointment> appointments = List.of(
            Appointment.builder().id("app1").build()
        );

        when(userMapper.toEntity(professional)).thenReturn(userEntity);
        when(appointmentJpaRepository.findByHealthcareProfessionalAndScheduledDateTimeBetweenOrderByScheduledDateTime(
            userEntity, start, end)).thenReturn(entities);
        when(appointmentMapper.toDomain(entities.get(0))).thenReturn(appointments.get(0));

        List<Appointment> result = appointmentRepository.findAppointmentsByProfessionalAndDateRange(
            professional, start, end);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userMapper).toEntity(professional);
        verify(appointmentJpaRepository).findByHealthcareProfessionalAndScheduledDateTimeBetweenOrderByScheduledDateTime(
            userEntity, start, end);
        verify(appointmentMapper).toDomain(entities.get(0));
    }

    @Test
    void shouldFindAppointmentsByStatusAndDateRange() {
        AppointmentStatus status = AppointmentStatus.SCHEDULED;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        List<AppointmentEntity> entities = List.of(
            AppointmentEntity.builder()
                .id("app1")
                .status(status)
                .scheduledDateTime(start.plusDays(1))
                .build()
        );

        List<Appointment> appointments = List.of(
            Appointment.builder().id("app1").status("SCHEDULED").build()
        );

        when(appointmentJpaRepository.findByStatusAndScheduledDateTimeBetween(status, start, end))
            .thenReturn(entities);
        when(appointmentMapper.toDomain(entities.get(0))).thenReturn(appointments.get(0));

        List<Appointment> result = appointmentRepository.findAppointmentsByStatusAndDateRange(status, start, end);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SCHEDULED", result.get(0).status());

        verify(appointmentJpaRepository).findByStatusAndScheduledDateTimeBetween(status, start, end);
        verify(appointmentMapper).toDomain(entities.get(0));
    }

    @Test
    void shouldFindAppointmentsByPatientIdAndStatus() {
        String patientId = "patient1";
        AppointmentStatus status = AppointmentStatus.COMPLETED;

        List<AppointmentEntity> entities = List.of(
            AppointmentEntity.builder()
                .id("app1")
                .status(status)
                .build()
        );

        List<Appointment> appointments = List.of(
            Appointment.builder().id("app1").status("COMPLETED").build()
        );

        when(appointmentJpaRepository.findByPatientIdAndStatus(patientId, status))
            .thenReturn(entities);
        when(appointmentMapper.toDomain(entities.get(0))).thenReturn(appointments.get(0));

        List<Appointment> result = appointmentRepository.findByPatientIdAndStatus(patientId, status);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("COMPLETED", result.get(0).status());

        verify(appointmentJpaRepository).findByPatientIdAndStatus(patientId, status);
        verify(appointmentMapper).toDomain(entities.get(0));
    }

    @Test
    void shouldFindAppointmentsByProfessionalAndDate() {
        String professionalId = "prof1";
        LocalDateTime date = LocalDateTime.now();

        List<AppointmentEntity> entities = List.of(
            AppointmentEntity.builder()
                .id("app1")
                .scheduledDateTime(date)
                .build()
        );

        List<Appointment> appointments = List.of(
            Appointment.builder().id("app1").scheduledDateTime(date).build()
        );

        when(appointmentJpaRepository.findByProfessionalAndDate(professionalId, date))
            .thenReturn(entities);
        when(appointmentMapper.toDomain(entities.get(0))).thenReturn(appointments.get(0));

        List<Appointment> result = appointmentRepository.findByProfessionalAndDate(professionalId, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(date, result.get(0).scheduledDateTime());

        verify(appointmentJpaRepository).findByProfessionalAndDate(professionalId, date);
        verify(appointmentMapper).toDomain(entities.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoAppointmentsFound() {
        Patient patient = Patient.builder().id("patient1").build();
        PatientEntity patientEntity = PatientEntity.builder().id("patient1").build();

        when(patientMapper.toEntity(patient)).thenReturn(patientEntity);
        when(appointmentJpaRepository.findByPatientOrderByScheduledDateTimeDesc(patientEntity))
            .thenReturn(List.of());

        List<Appointment> result = appointmentRepository.findByPatientOrderByScheduledDateTimeDesc(patient);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(appointmentMapper, never()).toDomain(any());
    }
}