package com.persou.prontosus.domain;

import com.persou.prontosus.domain.valueobject.Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record Patient(
    String id,
    String cpf,
    String fullName,
    LocalDate birthDate,
    String gender,
    String phoneNumber,
    String email,
    Address address,
    String emergencyContactName,
    String emergencyContactPhone,
    String knownAllergies,
    String currentMedications,
    String chronicConditions,
    List<MedicalRecord> medicalRecords,
    List<Appointment> appointments,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}