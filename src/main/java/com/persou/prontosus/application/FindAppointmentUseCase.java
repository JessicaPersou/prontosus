package com.persou.prontosus.application;

import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.gateway.AppointmentRepository;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.gateway.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public List<Appointment> findByPatientId(String patientId) {
        log.info("Buscando agendamentos do paciente: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado: " + patientId));

        List<Appointment> appointments = appointmentRepository.findByPatientOrderByScheduledDateTimeDesc(patient);
        log.info("Encontrados {} agendamentos para o paciente {}", appointments.size(), patientId);

        return appointments;
    }

    public List<Appointment> findByProfessionalId(String professionalId) {
        log.info("Buscando agendamentos do profissional: {}", professionalId);

        User professional = userRepository.findById(professionalId)
            .orElseThrow(() -> new RuntimeException("Profissional não encontrado: " + professionalId));

        return appointmentRepository.findAppointmentsByProfessionalAndDateRange(
            professional,
            LocalDateTime.now().minusYears(1),
            LocalDateTime.now().plusYears(1)
        );
    }

    public List<Appointment> findByProfessionalAndDateRange(String professionalId, LocalDateTime start, LocalDateTime end) {
        log.info("Buscando agendamentos do profissional {} entre {} e {}", professionalId, start, end);

        User professional = userRepository.findById(professionalId)
            .orElseThrow(() -> new RuntimeException("Profissional não encontrado: " + professionalId));

        return appointmentRepository.findAppointmentsByProfessionalAndDateRange(professional, start, end);
    }

    public List<Appointment> findByStatusAndDateRange(String status, LocalDateTime start, LocalDateTime end) {
        log.info("Buscando agendamentos com status {} entre {} e {}", status, start, end);

        AppointmentStatus appointmentStatus;
        try {
            appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }

        return appointmentRepository.findAppointmentsByStatusAndDateRange(appointmentStatus, start, end);
    }

    public List<Appointment> findByPatientIdAndStatus(String patientId, String status) {
        log.info("Buscando agendamentos do paciente {} com status {}", patientId, status);

        AppointmentStatus appointmentStatus;
        try {
            appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }

        return appointmentRepository.findByPatientIdAndStatus(patientId, appointmentStatus);
    }

    public List<Appointment> findByProfessionalAndDate(String professionalId, LocalDateTime date) {
        log.info("Buscando agendamentos do profissional {} para a data {}", professionalId, date);

        return appointmentRepository.findByProfessionalAndDate(professionalId, date);
    }
}