package com.persou.prontosus.application;

import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.gateway.AppointmentRepository;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.gateway.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    private FindAppointmentUseCase findAppointmentUseCase;

    @BeforeEach
    void setUp() {
        findAppointmentUseCase = new FindAppointmentUseCase(
            appointmentRepository, patientRepository, userRepository);
    }

    @Test
    void shouldFindAppointmentsByPatientId() {
        String patientId = "patient1";

        Patient patient = Patient.builder()
            .id(patientId)
            .fullName("Test Patient")
            .build();

        List<Appointment> appointments = List.of(
            Appointment.builder()
                .id("app1")
                .patient(patient)
                .scheduledDateTime(LocalDateTime.now())
                .build(),
            Appointment.builder()
                .id("app2")
                .patient(patient)
                .scheduledDateTime(LocalDateTime.now().plusDays(1))
                .build()
        );

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientOrderByScheduledDateTimeDesc(patient))
            .thenReturn(appointments);

        List<Appointment> result = findAppointmentUseCase.findByPatientId(patientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("app1", result.get(0).id());
        assertEquals("app2", result.get(1).id());

        verify(patientRepository).findById(patientId);
        verify(appointmentRepository).findByPatientOrderByScheduledDateTimeDesc(patient);
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFoundForPatientAppointments() {
        String patientId = "nonexistent";

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> findAppointmentUseCase.findByPatientId(patientId));

        verify(patientRepository).findById(patientId);
        verify(appointmentRepository, never()).findByPatientOrderByScheduledDateTimeDesc(any());
    }

    @Test
    void shouldFindAppointmentsByProfessionalId() {
        String professionalId = "prof1";

        User professional = User.builder()
            .id(professionalId)
            .fullName("Dr. Test")
            .build();

        List<Appointment> appointments = List.of(
            Appointment.builder()
                .id("app1")
                .healthcareProfessional(professional)
                .build()
        );

        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(appointmentRepository.findAppointmentsByProfessionalAndDateRange(
            eq(professional), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(appointments);

        List<Appointment> result = findAppointmentUseCase.findByProfessionalId(professionalId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("app1", result.get(0).id());

        verify(userRepository).findById(professionalId);
        verify(appointmentRepository).findAppointmentsByProfessionalAndDateRange(
            eq(professional), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void shouldThrowExceptionWhenProfessionalNotFound() {
        String professionalId = "nonexistent";

        when(userRepository.findById(professionalId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> findAppointmentUseCase.findByProfessionalId(professionalId));

        verify(userRepository).findById(professionalId);
        verify(appointmentRepository, never()).findAppointmentsByProfessionalAndDateRange(any(), any(), any());
    }

    @Test
    void shouldFindAppointmentsByProfessionalAndDateRange() {
        String professionalId = "prof1";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        User professional = User.builder()
            .id(professionalId)
            .fullName("Dr. Test")
            .build();

        List<Appointment> appointments = List.of(
            Appointment.builder()
                .id("app1")
                .healthcareProfessional(professional)
                .scheduledDateTime(start.plusDays(1))
                .build()
        );

        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(appointmentRepository.findAppointmentsByProfessionalAndDateRange(professional, start, end))
            .thenReturn(appointments);

        List<Appointment> result = findAppointmentUseCase.findByProfessionalAndDateRange(professionalId, start, end);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).findById(professionalId);
        verify(appointmentRepository).findAppointmentsByProfessionalAndDateRange(professional, start, end);
    }

    @Test
    void shouldFindAppointmentsByStatusAndDateRange() {
        String status = "SCHEDULED";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        List<Appointment> appointments = List.of(
            Appointment.builder()
                .id("app1")
                .status(status)
                .scheduledDateTime(start.plusDays(1))
                .build()
        );

        when(appointmentRepository.findAppointmentsByStatusAndDateRange(AppointmentStatus.SCHEDULED, start, end))
            .thenReturn(appointments);

        List<Appointment> result = findAppointmentUseCase.findByStatusAndDateRange(status, start, end);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository).findAppointmentsByStatusAndDateRange(AppointmentStatus.SCHEDULED, start, end);
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        String invalidStatus = "INVALID_STATUS";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);

        assertThrows(IllegalArgumentException.class,
            () -> findAppointmentUseCase.findByStatusAndDateRange(invalidStatus, start, end));

        verify(appointmentRepository, never()).findAppointmentsByStatusAndDateRange(any(), any(), any());
    }

    @Test
    void shouldFindAppointmentsByPatientIdAndStatus() {
        String patientId = "patient1";
        String status = "SCHEDULED";

        List<Appointment> appointments = List.of(
            Appointment.builder()
                .id("app1")
                .status(status)
                .build()
        );

        when(appointmentRepository.findByPatientIdAndStatus(patientId, AppointmentStatus.SCHEDULED))
            .thenReturn(appointments);

        List<Appointment> result = findAppointmentUseCase.findByPatientIdAndStatus(patientId, status);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository).findByPatientIdAndStatus(patientId, AppointmentStatus.SCHEDULED);
    }

    @Test
    void shouldFindAppointmentsByProfessionalAndDate() {
        String professionalId = "prof1";
        LocalDateTime date = LocalDateTime.now();

        List<Appointment> appointments = List.of(
            Appointment.builder()
                .id("app1")
                .scheduledDateTime(date)
                .build()
        );

        when(appointmentRepository.findByProfessionalAndDate(professionalId, date))
            .thenReturn(appointments);

        List<Appointment> result = findAppointmentUseCase.findByProfessionalAndDate(professionalId, date);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository).findByProfessionalAndDate(professionalId, date);
    }
}