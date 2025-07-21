package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.config.MedicalRecordControllerMockConfig;
import com.persou.prontosus.adapters.config.TestSecurityConfig;
import com.persou.prontosus.adapters.request.MedicalRecordRequest;
import com.persou.prontosus.adapters.response.MedicalRecordResponse;
import com.persou.prontosus.application.CreateMedicalRecordUseCase;
import com.persou.prontosus.application.UpdateMedicalRecordUseCase;
import com.persou.prontosus.application.ViewMedicalHistoryUseCase;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.valueobject.VitalSigns;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MedicalRecordController.class)
@Import({TestSecurityConfig.class, MedicalRecordControllerMockConfig.class})
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateMedicalRecordUseCase createMedicalRecordUseCase;

    @Autowired
    private UpdateMedicalRecordUseCase updateMedicalRecordUseCase;

    @Autowired
    private ViewMedicalHistoryUseCase viewMedicalHistoryUseCase;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Test
    void shouldCreateMedicalRecordSuccessfully() throws Exception {
        MedicalRecordRequest request = createValidMedicalRecordRequest();
        MedicalRecord medicalRecord = createMedicalRecord();
        MedicalRecordResponse response = createMedicalRecordResponse(medicalRecord);

        Mockito.when(createMedicalRecordUseCase.execute(anyString(),
                anyString(), any(MedicalRecord.class)))
            .thenReturn(medicalRecord);
        Mockito.when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(response);

        mockMvc.perform(post("/medical-records/patient/"+"patient-id-123"+"/professional/"+"professional-id-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(medicalRecord.id()));
    }

    @Test
    void shouldUpdateMedicalRecordSuccessfully() throws Exception {
        String recordId = "record-123";
        MedicalRecordRequest request = createValidMedicalRecordRequest();
        MedicalRecord medicalRecord = createMedicalRecord();
        MedicalRecordResponse response = createMedicalRecordResponse(medicalRecord);

        Mockito.when(updateMedicalRecordUseCase.execute(eq(recordId), any(MedicalRecord.class)))
            .thenReturn(medicalRecord);
        Mockito.when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(response);

        mockMvc.perform(put("/medical-records/" + recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(medicalRecord.id()));
    }

    @Test
    void shouldGetProfessionalRecordsSuccessfully() throws Exception {
        String professionalId = "user-id";
        MedicalRecord medicalRecord = createMedicalRecord();
        MedicalRecordResponse response = createMedicalRecordResponse(medicalRecord);

        Mockito.when(viewMedicalHistoryUseCase.getProfessionalRecords(professionalId)).thenReturn(List.of(medicalRecord));
        Mockito.when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(response);

        mockMvc.perform(get("/medical-records/professional/" + professionalId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(medicalRecord.id()));
    }

    @Test
    void shouldHandleExceptionWhenGettingPatientHistory() throws Exception {
        String patientId = "patient-123";
        Mockito.when(viewMedicalHistoryUseCase.getPatientHistory(patientId)).thenThrow(new RuntimeException("Erro ao buscar histórico"));

        mockMvc.perform(get("/medical-records/patient/" + patientId))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleExceptionWhenMappingMedicalRecordResponse() throws Exception {
        String patientId = "patient-123";
        MedicalRecord medicalRecord = createMedicalRecord();
        Mockito.when(viewMedicalHistoryUseCase.getPatientHistory(patientId)).thenReturn(List.of(medicalRecord));
        Mockito.when(medicalRecordMapper.toResponse(medicalRecord)).thenThrow(new RuntimeException("Erro ao mapear registro"));

        mockMvc.perform(get("/medical-records/patient/" + patientId))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleExceptionWhenCreatingMedicalRecord() throws Exception {
        MedicalRecordRequest request = createValidMedicalRecordRequest();
        String patientId = "patient-id-123";
        String professionalId = "professional-id-123";
        Mockito.when(createMedicalRecordUseCase.execute(anyString(), anyString(), any(MedicalRecord.class)))
            .thenThrow(new RuntimeException("Erro ao criar registro médico"));

        mockMvc.perform(post("/medical-records/patient/" + patientId + "/professional/" + professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());
    }

private MedicalRecordRequest createValidMedicalRecordRequest() {
    return MedicalRecordRequest.builder()
        .consultationDate(LocalDateTime.of(2025, 7, 20, 10, 0))
        .chiefComplaint("Dor de cabeça")
        .historyOfPresentIllness("Paciente relata dor há 2 dias")
        .physicalExamination("Exame físico normal")
        .vitalSigns(VitalSigns.builder().diastolicPressure(120).heartRate(70).build())
        .diagnosis("Cefaleia tensional")
        .treatment("Analgésico")
        .prescriptions("Dipirona 500mg")
        .observations("Paciente orientado a retornar se piora")
        .build();
    }

    private MedicalRecord createMedicalRecord() {
        return MedicalRecord.builder()
            .id("record-123")
            .patient(Patient.builder().id("test-patient-id").build())
            .healthcareProfessional(User.builder().id("user-id").build())
            .appointment(Appointment.builder().build())
            .consultationDate(LocalDateTime.of(2025, 7, 20, 10, 0))
            .diagnosis("Diagnóstico de rotina")
            .treatment("Tratamento padrão")
            .prescriptions("Observações gerais")
            .vitalSigns(null)
            .observations(null)
            .physicalExamination(null)
            .historyOfPresentIllness(null)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private MedicalRecordResponse createMedicalRecordResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
            record.id(),
            record.patient(),
            record.healthcareProfessional(),
            record.appointment(),
            record.consultationDate(),
            record.chiefComplaint(),
            record.diagnosis(),
            record.historyOfPresentIllness(),
            record.vitalSigns(),
            record.physicalExamination(),
            record.treatment(),
            record.prescriptions(),
            record.observations(),
            record.attachments(),
            record.createdAt(),
            record.updatedAt()
        );
    }
}
