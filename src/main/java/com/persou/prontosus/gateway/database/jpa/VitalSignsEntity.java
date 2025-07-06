package com.persou.prontosus.gateway.database.jpa;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignsEntity {
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Integer heartRate;
    private Double temperature;
    private Integer respiratoryRate;
    private Double weight;
    private Double height;
    private Double oxygenSaturation;
}
