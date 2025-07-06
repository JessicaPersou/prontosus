package com.persou.prontosus.adapters.response;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record AppointmentResponse(
    String id,
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
