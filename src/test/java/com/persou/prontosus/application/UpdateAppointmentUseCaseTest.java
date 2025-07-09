package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.domain.enums.AppointmentType;
import com.persou.prontosus.gateway.database.jpa.AppointmentEntity;
import com.persou.prontosus.gateway.database.jpa.repository.AppointmentJpaRepository;
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
class UpdateAppointmentUseCaseTest {

    @Mock
    private AppointmentJpaRepository appointmentJpaRepository;

    private UpdateAppointmentUseCase updateAppointmentUseCase;

    @BeforeEach
    void setUp() {
        updateAppointmentUseCase = new UpdateAppointmentUseCase(appointmentJpaRepository);
    }

    @Test
    void shouldUpdateAppointmentSuccessfully() {
        String appointmentId = "app1";
        LocalDateTime originalDateTime = LocalDateTime.now().plusDays(1);
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2);

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .scheduledDateTime(originalDateTime)
            .status(AppointmentStatus.SCHEDULED)
            .type(AppointmentType.CONSULTATION)
            .reason("Original reason")
            .notes("Original notes")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .scheduledDateTime(newDateTime)
            .status("IN_PROGRESS")
            .type("FOLLOW_UP")
            .reason("Updated reason")
            .notes("Updated notes")
            .build();

        AppointmentEntity savedEntity = existingEntity.toBuilder()
            .scheduledDateTime(newDateTime)
            .status(AppointmentStatus.IN_PROGRESS)
            .type(AppointmentType.FOLLOW_UP)
            .reason("Updated reason")
            .notes("Updated notes")
            .updatedAt(LocalDateTime.now())
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));
        when(appointmentJpaRepository.save(any(AppointmentEntity.class))).thenReturn(savedEntity);

        Appointment result = updateAppointmentUseCase.execute(appointmentId, updatedAppointment);

        assertNotNull(result);
        assertEquals(appointmentId, result.id());
        assertEquals(newDateTime, result.scheduledDateTime());
        assertEquals("IN_PROGRESS", result.status());
        assertEquals("FOLLOW_UP", result.type());
        assertEquals("Updated reason", result.reason());
        assertEquals("Updated notes", result.notes());

        verify(appointmentJpaRepository).findById(appointmentId);
        verify(appointmentJpaRepository).save(any(AppointmentEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenAppointmentNotFound() {
        String appointmentId = "nonexistent";

        Appointment updatedAppointment = Appointment.builder()
            .status("COMPLETED")
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> updateAppointmentUseCase.execute(appointmentId, updatedAppointment));

        assertNotNull(exception.getMessage());

        verify(appointmentJpaRepository).findById(appointmentId);
        verify(appointmentJpaRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        String appointmentId = "app1";

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .scheduledDateTime(LocalDateTime.now().plusDays(1))
            .status(AppointmentStatus.SCHEDULED)
            .type(AppointmentType.CONSULTATION)
            .reason("Original reason")
            .notes("Original notes")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .status("COMPLETED")
            .build();

        AppointmentEntity savedEntity = existingEntity.toBuilder()
            .status(AppointmentStatus.COMPLETED)
            .updatedAt(LocalDateTime.now())
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));
        when(appointmentJpaRepository.save(any(AppointmentEntity.class))).thenReturn(savedEntity);

        Appointment result = updateAppointmentUseCase.execute(appointmentId, updatedAppointment);

        assertNotNull(result);
        assertEquals("COMPLETED", result.status());

        verify(appointmentJpaRepository).save(argThat(entity ->
            AppointmentStatus.COMPLETED.equals(entity.getStatus()) &&
                entity.getScheduledDateTime().equals(existingEntity.getScheduledDateTime()) &&
                entity.getType().equals(existingEntity.getType()) &&
                "Original reason".equals(entity.getReason()) &&
                "Original notes".equals(entity.getNotes())));
    }

    @Test
    void shouldValidateInvalidStatus() {
        String appointmentId = "app1";

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .status(AppointmentStatus.SCHEDULED)
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .status("INVALID_STATUS")
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> updateAppointmentUseCase.execute(appointmentId, updatedAppointment));

        assertTrue(exception.getMessage().contains("Status inválido"));
        assertTrue(exception.getMessage().contains("INVALID_STATUS"));

        verify(appointmentJpaRepository).findById(appointmentId);
        verify(appointmentJpaRepository, never()).save(any());
    }

    @Test
    void shouldValidateInvalidType() {
        String appointmentId = "app1";

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .type(AppointmentType.CONSULTATION)
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .type("INVALID_TYPE")
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> updateAppointmentUseCase.execute(appointmentId, updatedAppointment));

        assertTrue(exception.getMessage().contains("Tipo inválido"));
        assertTrue(exception.getMessage().contains("INVALID_TYPE"));

        verify(appointmentJpaRepository).findById(appointmentId);
        verify(appointmentJpaRepository, never()).save(any());
    }

    @Test
    void shouldUpdateScheduledDateTime() {
        String appointmentId = "app1";
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(3);

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .scheduledDateTime(LocalDateTime.now().plusDays(1))
            .status(AppointmentStatus.SCHEDULED)
            .type(AppointmentType.CONSULTATION)
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .scheduledDateTime(newDateTime)
            .build();

        AppointmentEntity savedEntity = existingEntity.toBuilder()
            .scheduledDateTime(newDateTime)
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));
        when(appointmentJpaRepository.save(any(AppointmentEntity.class))).thenReturn(savedEntity);

        Appointment result = updateAppointmentUseCase.execute(appointmentId, updatedAppointment);

        assertNotNull(result);
        assertEquals(newDateTime, result.scheduledDateTime());

        verify(appointmentJpaRepository).save(argThat(entity ->
            newDateTime.equals(entity.getScheduledDateTime())));
    }

    @Test
    void shouldUpdateReasonAndNotes() {
        String appointmentId = "app1";

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .reason("Old reason")
            .notes("Old notes")
            .status(AppointmentStatus.SCHEDULED)
            .type(AppointmentType.CONSULTATION)
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .reason("New reason")
            .notes("New notes")
            .build();

        AppointmentEntity savedEntity = existingEntity.toBuilder()
            .reason("New reason")
            .notes("New notes")
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));
        when(appointmentJpaRepository.save(any(AppointmentEntity.class))).thenReturn(savedEntity);

        Appointment result = updateAppointmentUseCase.execute(appointmentId, updatedAppointment);

        assertNotNull(result);
        assertEquals("New reason", result.reason());
        assertEquals("New notes", result.notes());

        verify(appointmentJpaRepository).save(argThat(entity ->
            "New reason".equals(entity.getReason()) &&
                "New notes".equals(entity.getNotes())));
    }

    @Test
    void shouldUpdateUpdatedAtTimestamp() {
        String appointmentId = "app1";
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusHours(1);

        AppointmentEntity existingEntity = AppointmentEntity.builder()
            .id(appointmentId)
            .status(AppointmentStatus.SCHEDULED)
            .type(AppointmentType.CONSULTATION)
            .updatedAt(originalUpdatedAt)
            .build();

        Appointment updatedAppointment = Appointment.builder()
            .status("COMPLETED")
            .build();

        when(appointmentJpaRepository.findById(appointmentId)).thenReturn(Optional.of(existingEntity));
        when(appointmentJpaRepository.save(any(AppointmentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        updateAppointmentUseCase.execute(appointmentId, updatedAppointment);

        verify(appointmentJpaRepository).save(argThat(entity ->
            entity.getUpdatedAt().isAfter(originalUpdatedAt)));
    }
}