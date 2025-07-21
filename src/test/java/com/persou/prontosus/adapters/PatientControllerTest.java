package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.config.PatientControllerMockConfig;
import com.persou.prontosus.adapters.config.TestSecurityConfig;
import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.adapters.response.PatientResponse;
import com.persou.prontosus.application.FindPatientUseCase;
import com.persou.prontosus.application.RegisterPatientUseCase;
import com.persou.prontosus.application.UpdatePatientUseCase;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.config.security.JwtService;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.valueobject.Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PatientController.class)
@Import({TestSecurityConfig.class, PatientControllerMockConfig.class})
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FindPatientUseCase findPatientUseCase;

    @Autowired
    private RegisterPatientUseCase registerPatientUseCase;

    @Autowired
    private UpdatePatientUseCase updatePatientUseCase;

    @Autowired
    private PatientMapper patientMapper;

    @Test
    void shouldCreatePatientSuccessfully() throws Exception {
        PatientRequest request = createValidPatientRequest();

        Patient savedPatient = createPatientFromRequest(request);

        PatientResponse expectedResponse = createPatientResponse(savedPatient);

        Mockito.when(registerPatientUseCase.execute(any(Patient.class)))
            .thenReturn(savedPatient);

        Mockito.when(patientMapper.toResponse(savedPatient))
            .thenReturn(expectedResponse);

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(savedPatient.id()))
            .andExpect(jsonPath("$.cpf").value(savedPatient.cpf()))
            .andExpect(jsonPath("$.fullName").value(savedPatient.fullName()))
            .andExpect(jsonPath("$.email").value(savedPatient.email()));
    }

    @Test
    void shouldReturnAllPatients() throws Exception {
        var patient = createPatient();
        Mockito.when(findPatientUseCase.findAll()).thenReturn(List.of(patient));
        Mockito.when(patientMapper.toResponse(any(Patient.class))).thenReturn(createPatientResponse(patient));

        mockMvc.perform(get("/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(patient.id()));
    }

    @Test
    void shouldReturnPatientById() throws Exception {
        var patient = createPatient();
        Mockito.when(findPatientUseCase.findById(patient.id())).thenReturn(java.util.Optional.of(patient));
        Mockito.when(patientMapper.toResponse(any(Patient.class))).thenReturn(createPatientResponse(patient));

        mockMvc.perform(get("/patients/" + patient.id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(patient.id()));
    }

    @Test
    void shouldReturnPatientByCpf() throws Exception {
        var patient = createPatient();
        Mockito.when(findPatientUseCase.findByCpf(patient.cpf())).thenReturn(java.util.Optional.of(patient));
        Mockito.when(patientMapper.toResponse(any(Patient.class))).thenReturn(createPatientResponse(patient));

        mockMvc.perform(get("/patients/cpf/" + patient.cpf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cpf").value(patient.cpf()));
    }

    @Test
    void shouldReturnPatientsByName() throws Exception {
        var patient = createPatient();
        Mockito.when(findPatientUseCase.findByName(patient.fullName())).thenReturn(List.of(patient));
        Mockito.when(patientMapper.toResponse(any(Patient.class))).thenReturn(createPatientResponse(patient));

        mockMvc.perform(get("/patients/search").param("name", patient.fullName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fullName").value(patient.fullName()));
    }

    @Test
    void shouldUpdatePatientSuccessfully() throws Exception {
        var patient = createPatient();
        var request = new PatientRequest(
            patient.cpf(), patient.fullName(), patient.birthDate(), patient.gender(), patient.phoneNumber(),
            patient.email(), patient.address(), patient.emergencyContactName(), patient.emergencyContactPhone(),
            patient.knownAllergies(), patient.currentMedications(), patient.chronicConditions()
        );
        Mockito.when(updatePatientUseCase.execute(eq(patient.id()), any(Patient.class))).thenReturn(patient);
        Mockito.when(patientMapper.toResponse(any(Patient.class))).thenReturn(createPatientResponse(patient));

        mockMvc.perform(put("/patients/" + patient.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(patient.id()));
    }

    private PatientRequest createValidPatientRequest() {
        return new PatientRequest(
            "12345678901",
            "João da Silva",
            LocalDate.of(1990, 5, 15),
            "MALE",
            "11987654321",
            "joao.silva@email.com",
            createValidAddress(),
            "Maria da Silva",
            "11976543210",
            "Nenhuma alergia conhecida",
            "Nenhum medicamento",
            "Nenhuma condição crônica"
        );
    }

    private Address createValidAddress() {
        return Address.builder()
            .zipCode("01310100")
            .street("Avenida Paulista")
            .number("1000")
            .complement("Apto 101")
            .neighborhood("Bela Vista")
            .city("São Paulo")
            .state("SP")
            .build();
    }

    private Patient createPatientFromRequest(PatientRequest request) {
        return Patient.builder()
            .id("generated-patient-id-123")
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
    }

    private PatientResponse createPatientResponse(Patient patient) {
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

    private Patient createPatient() {
        return Patient.builder()
            .id("test-id-123")
            .cpf("12345678901")
            .fullName("João da Silva")
            .birthDate(java.time.LocalDate.of(1990, 5, 15))
            .gender("MALE")
            .phoneNumber("11987654321")
            .email("joao.silva@email.com")
            .address(createValidAddress())
            .emergencyContactName("Maria da Silva")
            .emergencyContactPhone("11976543210")
            .knownAllergies("Nenhuma alergia conhecida")
            .currentMedications("Nenhum medicamento")
            .chronicConditions("Nenhuma condição crônica")
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now())
            .build();
    }
}