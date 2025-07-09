package com.persou.prontosus.adapters.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record AppointmentRequest(
    @NotBlank(message = "ID do paciente é obrigatório")
    String patientId,

    @NotBlank(message = "ID do profissional é obrigatório")
    String healthcareProfessionalId,

    @NotNull(message = "Data e hora do agendamento são obrigatórias")
    LocalDateTime scheduledDateTime,

    @NotBlank(message = "Status é obrigatório")
    String status,

    @NotBlank(message = "Tipo de consulta é obrigatório")
    String type,

    String reason,
    String notes
) {
}