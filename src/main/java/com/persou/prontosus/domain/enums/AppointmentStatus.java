package com.persou.prontosus.domain.enums;

public enum AppointmentStatus {
    SCHEDULED("Agendado"),
    IN_PROGRESS("Em Andamento"),
    COMPLETED("Concluído"),
    CANCELLED("Cancelado"),
    NO_SHOW("Faltou");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}