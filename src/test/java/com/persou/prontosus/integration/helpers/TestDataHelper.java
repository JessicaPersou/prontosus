package com.persou.prontosus.integration.helpers;

import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.adapters.request.MedicalRecordRequest;
import com.persou.prontosus.adapters.request.AppointmentRequest;
import com.persou.prontosus.domain.valueobject.Address;
import com.persou.prontosus.domain.valueobject.VitalSigns;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataHelper {

    public static PatientRequest createValidPatientRequest() {
        return PatientRequest.builder()
            .cpf(generateCpf())
            .fullName("João da Silva Teste")
            .birthDate(LocalDate.of(1990, 1, 1))
            .gender("MALE")
            .phoneNumber("11987654321")
            .email("joao.teste@email.com")
            .address(Address.builder()
                .zipCode("01310100")
                .street("Avenida Paulista")
                .number("1000")
                .city("São Paulo")
                .state("SP")
                .build())
            .emergencyContactName("Maria da Silva")
            .emergencyContactPhone("11976543210")
            .knownAllergies("Penicilina")
            .currentMedications("Nenhuma")
            .chronicConditions("Nenhuma")
            .build();
    }

    public static MedicalRecordRequest createValidMedicalRecordRequest() {
        return MedicalRecordRequest.builder()
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Dor de cabeça")
            .historyOfPresentIllness("Paciente relata dor de cabeça há 2 dias")
            .physicalExamination("Paciente em bom estado geral")
            .vitalSigns(VitalSigns.builder()
                .systolicPressure(120)
                .diastolicPressure(80)
                .heartRate(75)
                .temperature(36.5)
                .respiratoryRate(16)
                .weight(70.0)
                .height(175.0)
                .oxygenSaturation(98.0)
                .build())
            .diagnosis("Cefaleia tensional")
            .treatment("Repouso e analgésicos")
            .prescriptions("Dipirona 500mg - 1 comprimido a cada 6 horas")
            .observations("Paciente orientado sobre sinais de alerta")
            .build();
    }

    public static AppointmentRequest createValidAppointmentRequest(String patientId, String professionalId) {
        return AppointmentRequest.builder()
            .patientId(patientId)
            .healthcareProfessionalId(professionalId)
            .scheduledDateTime(LocalDateTime.now().plusDays(1))
            .status("SCHEDULED")
            .type("CONSULTATION")
            .reason("Consulta de rotina")
            .notes("Primeira consulta")
            .build();
    }

    private static String generateCpf() {
        return String.valueOf(System.currentTimeMillis()).substring(3) + "01";
    }
}