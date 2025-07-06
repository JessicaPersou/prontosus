package com.persou.prontosus.adapters.request;

import com.persou.prontosus.domain.valueobject.VitalSigns;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record MedicalRecordRequest(
    LocalDateTime consultationDate,

    @NotBlank(message = "Queixa principal é obrigatória")
    String chiefComplaint,

    String historyOfPresentIllness,
    String physicalExamination,
    VitalSigns vitalSigns,
    String diagnosis,
    String treatment,
    String prescriptions,
    String observations
) {
}
