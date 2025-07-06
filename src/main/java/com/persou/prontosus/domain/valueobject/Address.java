package com.persou.prontosus.domain.valueobject;

import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record Address(
    String zipCode,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state
) {
    public Address {
        if (zipCode != null && !zipCode.matches("\\d{8}")) {
            throw new IllegalArgumentException("CEP deve conter 8 dígitos");
        }
    }
}