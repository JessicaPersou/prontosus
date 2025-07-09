package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.request.MedicalRecordRequest;
import com.persou.prontosus.application.CreateMedicalRecordUseCase;
import com.persou.prontosus.application.UpdateMedicalRecordUseCase;
import com.persou.prontosus.application.ViewMedicalHistoryUseCase;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.valueobject.VitalSigns;
import com.persou.prontosus.mocks.MedicalRecordMock;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MedicalRecordController.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateMedicalRecordUseCase createMedicalRecordUseCase;

    @MockitoBean
    private UpdateMedicalRecordUseCase updateMedicalRecordUseCase;

    @MockitoBean
    private ViewMedicalHistoryUseCase viewMedicalHistoryUseCase;

    @MockitoBean
    private MedicalRecordMapper medicalRecordMapper;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldGetPatientHistorySuccessfully() throws Exception {
        // Given
        String patientId = "patient-id";
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomain();
        List<MedicalRecord> records = List.of(medicalRecord);

        when(viewMedicalHistoryUseCase.getPatientHistory(patientId)).thenReturn(records);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/medical-records/patient/{patientId}", patientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldGetProfessionalRecordsSuccessfully() throws Exception {
        // Given
        String professionalId = "professional-id";
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomain();
        List<MedicalRecord> records = List.of(medicalRecord);

        when(viewMedicalHistoryUseCase.getProfessionalRecords(professionalId)).thenReturn(records);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/medical-records/professional/{professionalId}", professionalId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldCreateMedicalRecordSuccessfully() throws Exception {
        // Given
        String patientId = "patient-id";
        String professionalId = "professional-id";
        MedicalRecordRequest request = createValidMedicalRecordRequest();
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomain();

        when(createMedicalRecordUseCase.execute(eq(patientId), eq(professionalId), any(MedicalRecord.class)))
            .thenReturn(medicalRecord);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(post("/medical-records/patient/{patientId}/professional/{professionalId}",
                patientId, professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenChiefComplaintIsBlank() throws Exception {
        // Given
        String patientId = "patient-id";
        String professionalId = "professional-id";
        MedicalRecordRequest request = createValidMedicalRecordRequest()
            .withChiefComplaint("");

        // When & Then
        mockMvc.perform(post("/medical-records/patient/{patientId}/professional/{professionalId}",
                patientId, professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("chiefComplaint"))
            .andExpect(jsonPath("$.details[0].message").value("Queixa principal é obrigatória"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldCreateMedicalRecordWithVitalSigns() throws Exception {
        // Given
        String patientId = "patient-id";
        String professionalId = "professional-id";
        MedicalRecordRequest request = createMedicalRecordRequestWithVitalSigns();
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomainWithCompleteData();

        when(createMedicalRecordUseCase.execute(eq(patientId), eq(professionalId), any(MedicalRecord.class)))
            .thenReturn(medicalRecord);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(post("/medical-records/patient/{patientId}/professional/{professionalId}",
                patientId, professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldUpdateMedicalRecordSuccessfully() throws Exception {
        // Given
        String recordId = "record-id";
        MedicalRecordRequest request = createValidMedicalRecordRequest();
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomainForUpdate();

        when(updateMedicalRecordUseCase.execute(eq(recordId), any(MedicalRecord.class)))
            .thenReturn(medicalRecord);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(put("/medical-records/{recordId}", recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldCreateMedicalRecordWithMinimalData() throws Exception {
        // Given
        String patientId = "patient-id";
        String professionalId = "professional-id";
        MedicalRecordRequest request = MedicalRecordRequest.builder()
            .chiefComplaint("Consulta de rotina")
            .build();
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomainWithMinimalData();

        when(createMedicalRecordUseCase.execute(eq(patientId), eq(professionalId), any(MedicalRecord.class)))
            .thenReturn(medicalRecord);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(post("/medical-records/patient/{patientId}/professional/{professionalId}",
                patientId, professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToAccessMedicalRecords() throws Exception {
        // Given
        String patientId = "patient-id";
        MedicalRecord medicalRecord = MedicalRecordMock.mockDomain();
        List<MedicalRecord> records = List.of(medicalRecord);

        when(viewMedicalHistoryUseCase.getPatientHistory(patientId)).thenReturn(records);
        when(medicalRecordMapper.toResponse(medicalRecord)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/medical-records/patient/{patientId}", patientId))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "NURSE")
    void shouldReturnForbiddenWhenNurseTriesToAccessMedicalRecords() throws Exception {
        // Given
        String patientId = "patient-id";

        // When & Then
        mockMvc.perform(get("/medical-records/patient/{patientId}", patientId))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "NURSE")
    void shouldReturnForbiddenWhenNurseTriesToCreateMedicalRecord() throws Exception {
        // Given
        String patientId = "patient-id";
        String professionalId = "professional-id";
        MedicalRecordRequest request = createValidMedicalRecordRequest();

        // When & Then
        mockMvc.perform(post("/medical-records/patient/{patientId}/professional/{professionalId}",
                patientId, professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // Given
        String patientId = "patient-id";

        // When & Then
        mockMvc.perform(get("/medical-records/patient/{patientId}", patientId))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        // Given
        String patientId = "patient-id";
        String professionalId = "professional-id";

        // When & Then
        mockMvc.perform(post("/medical-records/patient/{patientId}/professional/{professionalId}",
                patientId, professionalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnEmptyListWhenNoRecordsFound() throws Exception {
        // Given
        String patientId = "patient-id";

        when(viewMedicalHistoryUseCase.getPatientHistory(patientId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/medical-records/patient/{patientId}", patientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldHandleMultipleMedicalRecords() throws Exception {
        // Given
        String patientId = "patient-id";
        MedicalRecord record1 = MedicalRecordMock.mockDomain();
        MedicalRecord record2 = MedicalRecordMock.mockDomainWithCompleteData();
        List<MedicalRecord> records = List.of(record1, record2);

        when(viewMedicalHistoryUseCase.getPatientHistory(patientId)).thenReturn(records);
        when(medicalRecordMapper.toResponse(record1)).thenReturn(any());
        when(medicalRecordMapper.toResponse(record2)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/medical-records/patient/{patientId}", patientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));
    }

    private MedicalRecordRequest createValidMedicalRecordRequest() {
        return MedicalRecordRequest.builder()
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Dor de cabeça persistente")
            .historyOfPresentIllness("Paciente relata cefaleia há 3 dias")
            .physicalExamination("Paciente em bom estado geral")
            .diagnosis("Cefaleia tensional")
            .treatment("Medicação sintomática e repouso")
            .prescriptions("Dipirona 500mg - 1 comprimido a cada 6 horas")
            .observations("Paciente orientada sobre sinais de alerta")
            .build();
    }

    private MedicalRecordRequest createMedicalRecordRequestWithVitalSigns() {
        return MedicalRecordRequest.builder()
            .consultationDate(LocalDateTime.now())
            .chiefComplaint("Dor abdominal intensa")
            .historyOfPresentIllness("Paciente relata dor abdominal há 2 dias")
            .physicalExamination("Abdome doloroso à palpação")
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
            .prescriptions("Jejum pré-operatório")
            .observations("Paciente orientado sobre procedimento cirúrgico")
            .build();
    }
}