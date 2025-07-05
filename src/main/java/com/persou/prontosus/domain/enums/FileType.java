package com.persou.prontosus.domain.enums;

public enum FileType {
    EXAM_RESULT("Resultado de Exame"),
    MEDICAL_REPORT("Relatório Médico"),
    IMAGE("Imagem"),
    PRESCRIPTION("Prescrição"),
    OTHER("Outro");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}