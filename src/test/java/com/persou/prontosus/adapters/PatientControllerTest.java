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
import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.application.FindPatientUseCase;
import com.persou.prontosus.application.RegisterPatientUseCase;
import com.persou.prontosus.application.UpdatePatientUseCase;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.valueobject.Address;
import com.persou.prontosus.mocks.PatientMock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FindPatientUseCase findPatientUseCase;

    @MockitoBean
    private RegisterPatientUseCase registerPatientUseCase;

    @MockitoBean
    private UpdatePatientUseCase updatePatientUseCase;

    @MockitoBean
    private PatientMapper patientMapper;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldFindAllPatients() throws Exception {
        // Given
        Patient patient = PatientMock.mockDomain();
        List<Patient> patients = List.of(patient);

        when(findPatientUseCase.findAll()).thenReturn(patients);
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldFindPatientById() throws Exception {
        // Given
        String patientId = "patient-id";
        Patient patient = PatientMock.mockDomain();

        when(findPatientUseCase.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/patients/{id}", patientId))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnNotFoundWhenPatientIdDoesNotExist() throws Exception {
        // Given
        String patientId = "nonexistent-id";

        when(findPatientUseCase.findById(patientId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/patients/{id}", patientId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").value("ResourceNotFound"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldFindPatientByCpf() throws Exception {
        // Given
        String cpf = "12345678901";
        Patient patient = PatientMock.mockDomain();

        when(findPatientUseCase.findByCpf(cpf)).thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/patients/cpf/{cpf}", cpf))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnNotFoundWhenCpfDoesNotExist() throws Exception {
        // Given
        String cpf = "99999999999";

        when(findPatientUseCase.findByCpf(cpf)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/patients/cpf/{cpf}", cpf))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Documento não encontrado: " + cpf));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldFindPatientsByName() throws Exception {
        // Given
        String name = "João";
        Patient patient = PatientMock.mockDomain();
        List<Patient> patients = List.of(patient);

        when(findPatientUseCase.findByName(name)).thenReturn(patients);
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/patients/search")
                .param("name", name))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldCreatePatientSuccessfully() throws Exception {
        // Given
        PatientRequest request = createValidPatientRequest();
        Patient patient = PatientMock.mockDomain();

        when(registerPatientUseCase.execute(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenCpfIsInvalid() throws Exception {
        // Given
        PatientRequest request = createValidPatientRequest()
            .withCpf("123"); // CPF inválido

        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("cpf"))
            .andExpect(jsonPath("$.details[0].message").value("CPF deve conter 11 dígitos"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenFullNameIsBlank() throws Exception {
        // Given
        PatientRequest request = createValidPatientRequest()
            .withFullName("");

        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("fullName"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // Given
        PatientRequest request = createValidPatientRequest()
            .withEmail("invalid-email");

        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("email"))
            .andExpect(jsonPath("$.details[0].message").value("Email deve ter formato válido"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenPhoneNumberIsInvalid() throws Exception {
        // Given
        PatientRequest request = createValidPatientRequest()
            .withPhoneNumber("123"); // Telefone muito curto

        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("phoneNumber"))
            .andExpect(jsonPath("$.details[0].message").value("Telefone deve conter entre 10 e 15 dígitos"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldUpdatePatientSuccessfully() throws Exception {
        // Given
        String patientId = "patient-id";
        PatientRequest request = createValidPatientRequest();
        Patient patient = PatientMock.mockDomain();

        when(updatePatientUseCase.execute(eq(patientId), any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(put("/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "NURSE")
    void shouldAllowNurseToAccessPatients() throws Exception {
        // Given
        Patient patient = PatientMock.mockDomain();
        List<Patient> patients = List.of(patient);

        when(findPatientUseCase.findAll()).thenReturn(patients);
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToAccessPatients() throws Exception {
        // Given
        Patient patient = PatientMock.mockDomain();
        List<Patient> patients = List.of(patient);

        when(findPatientUseCase.findAll()).thenReturn(patients);
        when(patientMapper.toResponse(patient)).thenReturn(any());

        // When & Then
        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER") // Role não autorizada
    void shouldReturnForbiddenWhenInsufficientRole() throws Exception {
        // When & Then
        mockMvc.perform(get("/patients"))
            .andExpect(status().isForbidden());
    }

    private PatientRequest createValidPatientRequest() {
        return PatientRequest.builder()
            .cpf("12345678901")
            .fullName("João Silva Santos")
            .birthDate(LocalDate.of(1990, 5, 15))
            .gender("MALE")
            .phoneNumber("11987654321")
            .email("joao.silva@email.com")
            .address(Address.builder()
                .zipCode("01310100")
                .street("Avenida Paulista")
                .number("1000")
                .neighborhood("Bela Vista")
                .city("São Paulo")
                .state("SP")
                .build())
            .emergencyContactName("Maria Silva")
            .emergencyContactPhone("11976543210")
            .knownAllergies("Penicilina")
            .currentMedications("Losartana 50mg")
            .chronicConditions("Hipertensão arterial")
            .build();
    }
}