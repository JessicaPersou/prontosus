package com.persou.prontosus.domain.enums;

public enum ProfessionalRole {
    DOCTOR("Médico"),
    NURSE("Enfermeiro"),
    ADMIN("Administrador");

    private final String description;

    ProfessionalRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
