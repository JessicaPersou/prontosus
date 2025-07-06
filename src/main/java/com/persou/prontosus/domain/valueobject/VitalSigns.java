package com.persou.prontosus.domain.valueobject;

import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record VitalSigns(
    Integer systolicPressure,
    Integer diastolicPressure,
    Integer heartRate,
    Double temperature,
    Integer respiratoryRate,
    Double weight,
    Double height,
    Double oxygenSaturation
) {
    public VitalSigns {
        if (systolicPressure != null && systolicPressure < 0) {
            throw new IllegalArgumentException("Pressão sistólica deve ser positiva");
        }
        if (diastolicPressure != null && diastolicPressure < 0) {
            throw new IllegalArgumentException("Pressão diastólica deve ser positiva");
        }
        if (heartRate != null && heartRate < 0) {
            throw new IllegalArgumentException("Frequência cardíaca deve ser positiva");
        }
    }
}
