package com.persou.prontosus.adapters.response;

import com.persou.prontosus.domain.valueobject.Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record PatientResponse(
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
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

