package com.persou.prontosus.adapters.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record UserRequest(
    String id,

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    String username,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    String password,

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 2, max = 200, message = "Nome completo deve ter entre 2 e 200 caracteres")
    String fullName,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    String email,

    @NotBlank(message = "Documento profissional é obrigatório")
    String professionalDocument,

    @NotBlank(message = "Cargo/Função é obrigatório")
    String role,

    String specialty,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {
}