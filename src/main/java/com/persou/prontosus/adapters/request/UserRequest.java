package com.persou.prontosus.adapters.request;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record UserRequest(
    String id,
    String username,
    String fullName,
    String email,
    String professionalDocument,
    String role,
    String specialty,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {}