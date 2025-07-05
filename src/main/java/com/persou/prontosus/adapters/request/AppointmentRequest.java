package com.persou.prontosus.adapters.request;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record AppointmentRequest(
    Long id,
    Patient patient,
    User healthcareProfessional,
    LocalDateTime scheduledDateTime,
    String status,
    String type,
    String reason,
    String notes,
    MedicalRecord medicalRecord,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
