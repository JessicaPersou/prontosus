package com.persou.prontosus.mocks;

import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.valueobject.VitalSigns;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.VitalSignsEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MedicalRecordMock {

    public static MedicalRecord mockDomain() {
        return MedicalRecord.builder()
            .id(UUID.randomUUID().toString())
            .patient(PatientMock.mockDomain())
            .healthcareProfessional(UserMock.mockDomain())
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Dor de cabeça persistente")
            .historyOfPresentIllness("Paciente relata cefaleia há 3 dias")
            .physicalExamination("Paciente em bom estado geral")
            .vitalSigns(mockVitalSigns())
            .diagnosis("Cefaleia tensional")
            .treatment("Medicação sintomática e repouso")
            .prescriptions("Dipirona 500mg - 1 comprimido a cada 6 horas")
            .observations("Paciente orientada sobre sinais de alerta")
            .attachments(List.of())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static MedicalRecordEntity mockEntity() {
        return MedicalRecordEntity.builder()
            .id(UUID.randomUUID().toString())
            .patient(PatientMock.mockEntity())
            .healthcareProfessional(UserMock.mockEntity())
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Dor de cabeça persistente")
            .historyOfPresentIllness("Paciente relata cefaleia há 3 dias")
            .physicalExamination("Paciente em bom estado geral")
            .vitalSigns(mockVitalSignsEntity())
            .diagnosis("Cefaleia tensional")
            .treatment("Medicação sintomática e repouso")
            .prescriptions("Dipirona 500mg - 1 comprimido a cada 6 horas")
            .observations("Paciente orientada sobre sinais de alerta")
            .attachments(List.of())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static VitalSigns mockVitalSigns() {
        return VitalSigns.builder()
            .systolicPressure(120)
            .diastolicPressure(80)
            .heartRate(75)
            .temperature(36.5)
            .respiratoryRate(16)
            .weight(70.0)
            .height(170.0)
            .oxygenSaturation(98.0)
            .build();
    }

    public static VitalSignsEntity mockVitalSignsEntity() {
        return new VitalSignsEntity(
            120,     // systolicPressure
            80,      // diastolicPressure
            75,      // heartRate
            36.5,    // temperature
            16,      // respiratoryRate
            70.0,    // weight
            170.0,   // height
            98.0     // oxygenSaturation
        );
    }

    public static List<MedicalRecordEntity> mockEntityList() {
        return List.of(mockEntity());
    }

    public static List<MedicalRecord> mockDomainList() {
        return List.of(mockDomain());
    }

    public static MedicalRecord mockDomainWithMinimalData() {
        return MedicalRecord.builder()
            .id(UUID.randomUUID().toString())
            .patient(PatientMock.mockDomain())
            .healthcareProfessional(UserMock.mockDomain())
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Consulta de rotina")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static MedicalRecord mockDomainWithCompleteData() {
        return MedicalRecord.builder()
            .id(UUID.randomUUID().toString())
            .patient(PatientMock.mockDomain())
            .healthcareProfessional(UserMock.mockDomain())
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Dor abdominal intensa")
            .historyOfPresentIllness(
                "Paciente relata dor abdominal há 2 dias, tipo cólica, localizada em fossa ilíaca direita")
            .physicalExamination("Abdome doloroso à palpação profunda em FID, sinal de Blumberg positivo")
            .vitalSigns(VitalSigns.builder()
                .systolicPressure(140)
                .diastolicPressure(90)
                .heartRate(95)
                .temperature(38.2)
                .respiratoryRate(20)
                .weight(68.0)
                .height(165.0)
                .oxygenSaturation(97.0)
                .build())
            .diagnosis("Apendicite aguda")
            .treatment("Cirurgia - apendicectomia laparoscópica")
            .prescriptions("Jejum pré-operatório\nAntibioticoprofilaxia: Cefazolina 2g EV")
            .observations("Paciente orientado sobre procedimento cirúrgico. Familiares cientes.")
            .attachments(List.of())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static MedicalRecord mockDomainForUpdate() {
        return MedicalRecord.builder()
            .id("existing-record-id")
            .patient(PatientMock.mockDomain())
            .healthcareProfessional(UserMock.mockDomain())
            .consultationDate(LocalDateTime.now().minusHours(2))
            .chiefComplaint("Dor de cabeça - ATUALIZADA")
            .historyOfPresentIllness("História atualizada após nova anamnese")
            .physicalExamination("Exame físico atualizado")
            .diagnosis("Diagnóstico revisado")
            .treatment("Plano terapêutico ajustado")
            .prescriptions("Prescrição revista")
            .observations("Observações adicionais após reavaliação")
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now())
            .build();
    }
}