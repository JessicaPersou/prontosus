package com.persou.prontosus.adapters.request;

import com.persou.prontosus.domain.valueobject.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record PatientRequest(
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    String cpf,

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 2, max = 200, message = "Nome deve ter entre 2 e 200 caracteres")
    String fullName,

    @NotNull(message = "Data de nascimento é obrigatória")
    LocalDate birthDate,

    @NotBlank(message = "Gênero é obrigatório")
    String gender,

    @Pattern(regexp = "\\d{10,15}", message = "Telefone deve conter entre 10 e 15 dígitos")
    String phoneNumber,

    @Email(message = "Email deve ter formato válido")
    String email,

    Address address,

    String emergencyContactName,

    @Pattern(regexp = "\\d{10,15}", message = "Telefone de emergência deve conter entre 10 e 15 dígitos")
    String emergencyContactPhone,

    String knownAllergies,
    String currentMedications,
    String chronicConditions
) {
}
