package com.persou.prontosus.gateway.database.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {

    @Column(length = 10)
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    private String zipCode;

    @Column(length = 200)
    private String street;

    @Column(length = 10)
    private String number;

    @Column(length = 100)
    private String complement;

    @Column(length = 100)
    private String neighborhood;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String state;
}