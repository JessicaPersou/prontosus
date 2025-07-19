package com.persou.prontosus.adapters;

import static com.persou.prontosus.config.MessagesErrorException.DOCUMENT_NOT_FOUND;
import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.adapters.response.PatientResponse;
import com.persou.prontosus.application.FindPatientUseCase;
import com.persou.prontosus.application.RegisterPatientUseCase;
import com.persou.prontosus.application.UpdatePatientUseCase;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.handler.ApiExceptionHandler;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.valueobject.Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PatientController.class)
@Import({ApiExceptionHandler.class, PatientControllerTest.TestConfig.class})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FindPatientUseCase findPatientUseCase;

    @Autowired
    private RegisterPatientUseCase registerPatientUseCase;

    @Autowired
    private UpdatePatientUseCase updatePatientUseCase;

    @Autowired
    private PatientMapper patientMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public FindPatientUseCase findPatientUseCase() {
            return Mockito.mock(FindPatientUseCase.class);
        }

        @Bean
        @Primary
        public RegisterPatientUseCase registerPatientUseCase() {
            return Mockito.mock(RegisterPatientUseCase.class);
        }

        @Bean
        @Primary
        public UpdatePatientUseCase updatePatientUseCase() {
            return Mockito.mock(UpdatePatientUseCase.class);
        }

        @Bean
        @Primary
        public PatientMapper patientMapper() {
            return Mockito.mock(PatientMapper.class);
        }
    }

    @Test
    void findAll_shouldReturnListOfPatients() throws Exception {
        Patient patient1 = createSamplePatient();
        Patient patient2 = createSamplePatient();

        Mockito.when(findPatientUseCase.findAll()).thenReturn(List.of(patient1, patient2));
        Mockito.when(patientMapper.toResponse(patient1)).thenReturn(createSampleResponse(patient1));
        Mockito.when(patientMapper.toResponse(patient2)).thenReturn(createSampleResponse(patient2));

        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(patient1.id())))
            .andExpect(jsonPath("$[1].id", is(patient2.id())));
    }

    @Test
    void findById_shouldReturnPatient_whenPatientExists() throws Exception {
        Patient patient = createSamplePatient();
        PatientResponse response = createSampleResponse(patient);

        Mockito.when(findPatientUseCase.findById(patient.id())).thenReturn(Optional.of(patient));
        Mockito.when(patientMapper.toResponse(patient)).thenReturn(response);

        mockMvc.perform(get("/patients/{id}", patient.id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(patient.id())))
            .andExpect(jsonPath("$.fullName", is(patient.fullName())));
    }

    @Test
    void findById_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        String nonExistentId = UUID.randomUUID().toString();
        Mockito.when(findPatientUseCase.findById(nonExistentId))
            .thenThrow(new ResourceNotFoundException(ENTITY_NOT_FOUND));

        mockMvc.perform(get("/patients/{id}", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString(ENTITY_NOT_FOUND)));
    }

    @Test
    void findByCpf_shouldReturnPatient_whenPatientExists() throws Exception {
        Patient patient = createSamplePatient();
        PatientResponse response = createSampleResponse(patient);

        Mockito.when(findPatientUseCase.findByCpf(patient.cpf())).thenReturn(Optional.of(patient));
        Mockito.when(patientMapper.toResponse(patient)).thenReturn(response);

        mockMvc.perform(get("/patients/cpf/{cpf}", patient.cpf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cpf", is(patient.cpf())))
            .andExpect(jsonPath("$.fullName", is(patient.fullName())));
    }

    @Test
    void findByCpf_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        String nonExistentCpf = "99999999999";
        Mockito.when(findPatientUseCase.findByCpf(nonExistentCpf))
            .thenThrow(new ResourceNotFoundException(DOCUMENT_NOT_FOUND + ": " + nonExistentCpf));

        mockMvc.perform(get("/patients/cpf/{cpf}", nonExistentCpf))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString(DOCUMENT_NOT_FOUND)));
    }

    @Test
    void findByName_shouldReturnMatchingPatients() throws Exception {
        String searchName = "Silva";
        Patient patient1 = createSamplePatient().withFullName("João Silva");
        Patient patient2 = createSamplePatient().withFullName("Maria Silva");

        Mockito.when(findPatientUseCase.findByName(searchName)).thenReturn(List.of(patient1, patient2));
        Mockito.when(patientMapper.toResponse(patient1)).thenReturn(createSampleResponse(patient1));
        Mockito.when(patientMapper.toResponse(patient2)).thenReturn(createSampleResponse(patient2));

        mockMvc.perform(get("/patients/search").param("name", searchName))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].fullName", is(patient1.fullName())))
            .andExpect(jsonPath("$[1].fullName", is(patient2.fullName())));
    }

    @Test
    void create_shouldRegisterNewPatient() throws Exception {
        PatientRequest request = new PatientRequest(
            "12345678901",
            "João da Silva",
            LocalDate.of(1980, 1, 1),
            "MALE",
            "11999999999",
            "joao@example.com",
            Address.builder()
                .street("Rua Silva Atualizada")
                .city("SP")
                .number("456")
                .zipCode("11111111")
                .neighborhood("Vila Feliz Atualizada").build(),
            "Maria Silva",
            "11988888888",
            "Pólen, amendoim",
            "Aspirina",
            "Hipertensão"
        );

        Patient patient = Patient.builder()
            .cpf(request.cpf())
            .fullName(request.fullName())
            .birthDate(request.birthDate())
            .gender(request.gender())
            .phoneNumber(request.phoneNumber())
            .email(request.email())
            .address(request.address())
            .emergencyContactName(request.emergencyContactName())
            .emergencyContactPhone(request.emergencyContactPhone())
            .knownAllergies(request.knownAllergies())
            .currentMedications(request.currentMedications())
            .chronicConditions(request.chronicConditions())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Patient savedPatient = patient.withId(UUID.randomUUID().toString());
        PatientResponse response = createSampleResponse(savedPatient);

        Mockito.when(registerPatientUseCase.execute(any(Patient.class))).thenReturn(savedPatient);
        Mockito.when(patientMapper.toResponse(savedPatient)).thenReturn(response);

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(savedPatient.id())))
            .andExpect(jsonPath("$.fullName", is(savedPatient.fullName())));
    }

    @Test
    void create_shouldReturnBadRequest_whenInvalidData() throws Exception {
        PatientRequest invalidRequest = new PatientRequest(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    void update_shouldUpdateExistingPatient() throws Exception {
        String patientId = UUID.randomUUID().toString();
        PatientRequest request = new PatientRequest(
            "12345678901",
            "João da Silva Updated",
            LocalDate.of(1980, 1, 1),
            "MALE",
            "11999999999",
            "joao.updated@example.com",
            Address.builder()
                .street("Rua Silva Atualizada")
                .city("SP")
                .number("456")
                .zipCode("11111111")
                .neighborhood("Vila Feliz Atualizada").build(),
            "Maria Silva Updated",
            "11988888888",
            "Pólen, amendoim, camarão",
            "Aspirina, Paracetamol",
            "Hipertensão, Diabetes"
        );

        Patient updatedPatient = Patient.builder()
            .id(patientId)
            .cpf(request.cpf())
            .fullName(request.fullName())
            .birthDate(request.birthDate())
            .gender(request.gender())
            .phoneNumber(request.phoneNumber())
            .email(request.email())
            .address(request.address())
            .emergencyContactName(request.emergencyContactName())
            .emergencyContactPhone(request.emergencyContactPhone())
            .knownAllergies(request.knownAllergies())
            .currentMedications(request.currentMedications())
            .chronicConditions(request.chronicConditions())
            .build();

        Patient savedPatient = updatedPatient.withUpdatedAt(LocalDateTime.now());
        PatientResponse response = createSampleResponse(savedPatient);

        Mockito.when(updatePatientUseCase.execute(anyString(), any(Patient.class))).thenReturn(savedPatient);
        Mockito.when(patientMapper.toResponse(savedPatient)).thenReturn(response);

        mockMvc.perform(put("/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(patientId)))
            .andExpect(jsonPath("$.fullName", is(request.fullName())))
            .andExpect(jsonPath("$.email", is(request.email())));
    }

    @Test
    void update_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        String nonExistentId = UUID.randomUUID().toString();
        PatientRequest request = new PatientRequest(
            "12345678901",
            "João da Silva",
            LocalDate.of(1980, 1, 1),
            "MALE",
            "11999999999",
            "joao@example.com",
            Address.builder()
                .street("Rua Silva Atualizada")
                .city("SP")
                .number("456")
                .zipCode("11111111")
                .neighborhood("Vila Feliz Atualizada").build(),
            "Maria Silva",
            "11988888888",
            "Pólen, amendoim",
            "Aspirina",
            "Hipertensão"
        );

        Mockito.when(updatePatientUseCase.execute(anyString(), any(Patient.class)))
            .thenThrow(new ResourceNotFoundException(ENTITY_NOT_FOUND));

        mockMvc.perform(put("/patients/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString(ENTITY_NOT_FOUND)));
    }

    @Test
    void update_shouldReturnBadRequest_whenInvalidData() throws Exception {
        String patientId = UUID.randomUUID().toString();
        PatientRequest invalidRequest = new PatientRequest(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(put("/patients/{id}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    private Patient createSamplePatient() {
        return Patient.builder()
            .id(UUID.randomUUID().toString())
            .cpf("12345678901")
            .fullName("João da Silva")
            .birthDate(LocalDate.of(1980, 1, 1))
            .gender("MALE")
            .phoneNumber("11999999999")
            .email("joao@example.com")
            .address(Address.builder()
                .street("Rua Silva Atualizada")
                .city("SP")
                .number("456")
                .zipCode("11111111")
                .neighborhood("Vila Feliz Atualizada").build())
            .emergencyContactName("Maria Silva")
            .emergencyContactPhone("11988888888")
            .knownAllergies("Pólen, amendoim")
            .currentMedications("Aspirina")
            .chronicConditions("Hipertensão")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private PatientResponse createSampleResponse(Patient patient) {
        return new PatientResponse(
            patient.id(),
            patient.cpf(),
            patient.fullName(),
            patient.birthDate(),
            patient.gender(),
            patient.phoneNumber(),
            patient.email(),
            patient.address(),
            patient.emergencyContactName(),
            patient.emergencyContactPhone(),
            patient.knownAllergies(),
            patient.currentMedications(),
            patient.chronicConditions(),
            patient.createdAt(),
            patient.updatedAt()
        );
    }
}