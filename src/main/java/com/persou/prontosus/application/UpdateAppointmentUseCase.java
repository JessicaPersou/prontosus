package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.domain.enums.AppointmentType;
import com.persou.prontosus.gateway.database.jpa.repository.AppointmentJpaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateAppointmentUseCase {

    private final AppointmentJpaRepository appointmentJpaRepository;

    @Transactional
    public Appointment execute(String appointmentId, Appointment updatedAppointment) {
        try {
            log.info("Atualizando agendamento: {}", appointmentId);

            var existingEntity = appointmentJpaRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    log.error("Agendamento não encontrado: {}", appointmentId);
                    return new ResourceNotFoundException(ENTITY_NOT_FOUND);
                });

            log.info("Agendamento encontrado: {}", existingEntity.getId());

            // Atualizar campos se fornecidos
            if (updatedAppointment.scheduledDateTime() != null) {
                existingEntity.setScheduledDateTime(updatedAppointment.scheduledDateTime());
                log.debug("Atualizando data/hora para: {}", updatedAppointment.scheduledDateTime());
            }

            if (updatedAppointment.status() != null) {
                AppointmentStatus status = validateStatus(updatedAppointment.status());
                existingEntity.setStatus(status);
                log.debug("Atualizando status para: {}", status);
            }

            if (updatedAppointment.type() != null) {
                AppointmentType type = validateType(updatedAppointment.type());
                existingEntity.setType(type);
                log.debug("Atualizando tipo para: {}", type);
            }

            if (updatedAppointment.reason() != null) {
                existingEntity.setReason(updatedAppointment.reason());
                log.debug("Atualizando motivo");
            }

            if (updatedAppointment.notes() != null) {
                existingEntity.setNotes(updatedAppointment.notes());
                log.debug("Atualizando observações");
            }

            existingEntity.setUpdatedAt(LocalDateTime.now());

            var savedEntity = appointmentJpaRepository.save(existingEntity);
            log.info("Agendamento atualizado com sucesso: {}", savedEntity.getId());

            // Converter de volta para domain
            return Appointment.builder()
                .id(savedEntity.getId())
                .scheduledDateTime(savedEntity.getScheduledDateTime())
                .status(savedEntity.getStatus().name())
                .type(savedEntity.getType().name())
                .reason(savedEntity.getReason())
                .notes(savedEntity.getNotes())
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();

        } catch (Exception e) {
            log.error("Erro ao atualizar agendamento {}: {}", appointmentId, e.getMessage(), e);
            throw e;
        }
    }

    private AppointmentStatus validateStatus(String status) {
        try {
            return AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status +
                ". Valores aceitos: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW");
        }
    }

    private AppointmentType validateType(String type) {
        try {
            return AppointmentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo inválido: " + type +
                ". Valores aceitos: CONSULTATION, FOLLOW_UP, EMERGENCY, PROCEDURE");
        }
    }
}