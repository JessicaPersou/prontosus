package com.persou.prontosus.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record User(
    String id,
    String username,
    String password,
    String fullName,
    String email,
    String professionalDocument,
    String role,
    String specialty,
    Boolean active,
    List<MedicalRecord> medicalRecords,
    List<Appointment> appointments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {
}