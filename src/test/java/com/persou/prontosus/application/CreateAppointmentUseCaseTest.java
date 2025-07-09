package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.AppointmentRepository;
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
class CreateAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    private CreateAppointmentUseCase createAppointmentUseCase;

    @BeforeEach
    void setUp() {
        createAppointmentUseCase = new CreateAppointmentUseCase(
            appointmentRepository, patientRepository, userRepository);
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
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

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now().plusDays(1))
            .status("SCHEDULED")
            .type("CONSULTATION")
            .reason("Test reason")
            .build();

        Appointment savedAppointment = appointment
            .withId("app1")
            .withPatient(patient)
            .withHealthcareProfessional(professional);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        Appointment result = createAppointmentUseCase.execute(patientId, professionalId, appointment);

        assertNotNull(result);
        assertEquals("app1", result.id());
        assertEquals(patient, result.patient());
        assertEquals(professional, result.healthcareProfessional());

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(professionalId);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        String patientId = "nonexistent";
        String professionalId = "prof1";

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now())
            .status("SCHEDULED")
            .type("CONSULTATION")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> createAppointmentUseCase.execute(patientId, professionalId, appointment));

        verify(patientRepository).findById(patientId);
        verify(userRepository, never()).findById(anyString());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenProfessionalNotFound() {
        String patientId = "patient1";
        String professionalId = "nonexistent";

        Patient patient = Patient.builder()
            .id(patientId)
            .fullName("Test Patient")
            .build();

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now())
            .status("SCHEDULED")
            .type("CONSULTATION")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> createAppointmentUseCase.execute(patientId, professionalId, appointment));

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(professionalId);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldValidateInvalidStatus() {
        String patientId = "patient1";
        String professionalId = "prof1";

        Patient patient = Patient.builder().id(patientId).build();
        User professional = User.builder().id(professionalId).build();

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now())
            .status("INVALID_STATUS")
            .type("CONSULTATION")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));

        assertThrows(IllegalArgumentException.class,
            () -> createAppointmentUseCase.execute(patientId, professionalId, appointment));
    }

    @Test
    void shouldValidateInvalidType() {
        String patientId = "patient1";
        String professionalId = "prof1";

        Patient patient = Patient.builder().id(patientId).build();
        User professional = User.builder().id(professionalId).build();

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now())
            .status("SCHEDULED")
            .type("INVALID_TYPE")
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));

        assertThrows(IllegalArgumentException.class,
            () -> createAppointmentUseCase.execute(patientId, professionalId, appointment));
    }

    @Test
    void shouldHandleNullStatusWithDefault() {
        String patientId = "patient1";
        String professionalId = "prof1";

        Patient patient = Patient.builder().id(patientId).build();
        User professional = User.builder().id(professionalId).build();

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now())
            .status(null)
            .type("CONSULTATION")
            .build();

        Appointment savedAppointment = appointment.withId("app1");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        Appointment result = createAppointmentUseCase.execute(patientId, professionalId, appointment);

        assertNotNull(result);
        verify(appointmentRepository).save(argThat(app ->
            "SCHEDULED".equals(app.status())));
    }

    @Test
    void shouldThrowExceptionWhenTypeIsNull() {
        String patientId = "patient1";
        String professionalId = "prof1";

        Patient patient = Patient.builder().id(patientId).build();
        User professional = User.builder().id(professionalId).build();

        Appointment appointment = Appointment.builder()
            .scheduledDateTime(LocalDateTime.now())
            .status("SCHEDULED")
            .type(null)
            .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));

        assertThrows(IllegalArgumentException.class,
            () -> createAppointmentUseCase.execute(patientId, professionalId, appointment));
    }
}