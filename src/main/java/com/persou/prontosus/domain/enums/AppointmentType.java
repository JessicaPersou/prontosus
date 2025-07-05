package com.persou.prontosus.domain.enums;

public enum AppointmentType {
    CONSULTATION("Consulta"),
    FOLLOW_UP("Retorno"),
    EMERGENCY("Emergência"),
    PROCEDURE("Procedimento");

    private final String description;

    AppointmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}