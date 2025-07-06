package com.persou.prontosus.adapters.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record LoginRequest(
    @NotBlank(message = "Nome de usuário é obrigatório")
    String username,

    @NotBlank(message = "Senha é obrigatória")
    String password
) {
}