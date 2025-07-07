package com.persou.prontosus.adapters.response;

import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record LoginResponse(
    String token,
    String type,
    Long expiresIn,
    UserInfo user
) {

    @With
    @Builder(toBuilder = true)
    public record UserInfo(
        String id,
        String username,
        String fullName,
        String email,
        String role,
        String specialty
    ) {
    }
}