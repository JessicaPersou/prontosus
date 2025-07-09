package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;
import static com.persou.prontosus.config.MessagesErrorException.HEALTHCARE_PROFESSIONAL_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.domain.enums.AppointmentType;
import com.persou.prontosus.gateway.AppointmentRepository;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.gateway.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public Appointment execute(String patientId, String healthcareProfessionalId, Appointment appointment) {
        log.info("Criando agendamento para paciente {} e profissional {}", patientId, healthcareProfessionalId);

        // Buscar paciente
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> {
                log.error("Paciente não encontrado: {}", patientId);
                return new ResourceNotFoundException(ENTITY_NOT_FOUND + " - Paciente: " + patientId);
            });

        // Buscar profissional
        User healthcareProfessional = userRepository.findById(healthcareProfessionalId)
            .orElseThrow(() -> {
                log.error("Profissional não encontrado: {}", healthcareProfessionalId);
                return new ResourceNotFoundException(HEALTHCARE_PROFESSIONAL_NOT_FOUND + " - ID: " + healthcareProfessionalId);
            });

        log.info("Paciente encontrado: {} - {}", patient.id(), patient.fullName());
        log.info("Profissional encontrado: {} - {}", healthcareProfessional.id(), healthcareProfessional.fullName());

        // Validar enums
        AppointmentStatus status = validateStatus(appointment.status());
        AppointmentType type = validateType(appointment.type());

        LocalDateTime now = LocalDateTime.now();

        // Criar o agendamento
        Appointment appointmentToSave = Appointment.builder()
            .patient(patient)
            .healthcareProfessional(healthcareProfessional)
            .scheduledDateTime(appointment.scheduledDateTime())
            .status(status.name())
            .type(type.name())
            .reason(appointment.reason())
            .notes(appointment.notes())
            .createdAt(now)
            .updatedAt(now)
            .build();

        log.info("Salvando agendamento...");
        Appointment savedAppointment = appointmentRepository.save(appointmentToSave);
        log.info("Agendamento criado com sucesso: {}", savedAppointment.id());

        return savedAppointment;
    }

    private AppointmentStatus validateStatus(String status) {
        if (status == null) {
            return AppointmentStatus.SCHEDULED; // Default
        }
        try {
            return AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status +
                ". Valores aceitos: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW");
        }
    }

    private AppointmentType validateType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de consulta é obrigatório");
        }
        try {
            return AppointmentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo inválido: " + type +
                ". Valores aceitos: CONSULTATION, FOLLOW_UP, EMERGENCY, PROCEDURE");
        }
    }
}