package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.config.AppointmentControllerMockConfig;
import com.persou.prontosus.adapters.config.TestSecurityConfig;
import com.persou.prontosus.adapters.request.AppointmentRequest;
import com.persou.prontosus.adapters.response.AppointmentResponse;
import com.persou.prontosus.application.CreateAppointmentUseCase;
import com.persou.prontosus.application.FindAppointmentUseCase;
import com.persou.prontosus.application.UpdateAppointmentUseCase;
import com.persou.prontosus.config.mapper.AppointmentMapper;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppointmentController.class)
@Import({TestSecurityConfig.class, AppointmentControllerMockConfig.class})
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateAppointmentUseCase createAppointmentUseCase;

    @Autowired
    private FindAppointmentUseCase findAppointmentUseCase;

    @Autowired
    private UpdateAppointmentUseCase updateAppointmentUseCase;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Test
    void shouldCreateAppointmentSuccessfully() throws Exception {
        AppointmentRequest request = createValidAppointmentRequest();
        Appointment savedAppointment = createAppointmentFromRequest(request);
        AppointmentResponse expectedResponse = createAppointmentResponse(savedAppointment);

        Mockito.when(createAppointmentUseCase.execute(
                eq(request.patientId()),
                eq(request.healthcareProfessionalId()),
                any(Appointment.class)))
            .thenReturn(savedAppointment);
        Mockito.when(appointmentMapper.toResponse(savedAppointment))
            .thenReturn(expectedResponse);

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(savedAppointment.id()));
    }

    @Test
    void shouldReturnAppointmentsByProfessionalIdAndDateRange() throws Exception {
        var professionalId = "doctor-id-456";
        var start = java.time.LocalDateTime.of(2025, 7, 20, 8, 0);
        var end = java.time.LocalDateTime.of(2025, 7, 20, 18, 0);
        var appointment = createAppointment();

        Mockito.when(findAppointmentUseCase.findByProfessionalAndDateRange(professionalId, start, end))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/professional/" + professionalId)
                .param("start", start.toString())
                .param("end", end.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointment.id()));
    }

    @Test
    void shouldReturnAppointmentsByProfessionalIdWithoutDateRange() throws Exception {
        var professionalId = "doctor-id-456";
        var appointment = createAppointment();

        Mockito.when(findAppointmentUseCase.findByProfessionalId(professionalId))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/professional/" + professionalId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointment.id()));
    }

    @Test
    void shouldReturnProfessionalAppointmentsByDate() throws Exception {
        var professionalId = "doctor-id-456";
        var date = java.time.LocalDateTime.of(2025, 7, 20, 10, 0);
        var appointment = createAppointment();

        Mockito.when(findAppointmentUseCase.findByProfessionalAndDate(professionalId, date))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/professional/" + professionalId + "/date/" + date.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointment.id()));
    }

    @Test
    void shouldReturnPatientAppointmentsByStatus() throws Exception {
        var appointment = createAppointment();
        var status = "AGENDADO";
        Mockito.when(findAppointmentUseCase.findByPatientIdAndStatus(appointment.patient().id(), status))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/patient/" + appointment.patient().id() + "/status/" + status))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointment.id()));
    }

    @Test
    void shouldReturnAppointmentsByStatusAndDateRange() throws Exception {
        var status = "AGENDADO";
        var start = java.time.LocalDateTime.of(2025, 7, 20, 8, 0);
        var end = java.time.LocalDateTime.of(2025, 7, 20, 18, 0);
        var appointment = createAppointment();

        Mockito.when(findAppointmentUseCase.findByStatusAndDateRange(status, start, end))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/status/" + status)
                .param("start", start.toString())
                .param("end", end.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointment.id()));
    }

    @Test
    void shouldReturnAllAppointments() throws Exception {
        var appointment = createAppointment();
        var appoinments = appointment.patient().id();
        Mockito.when(findAppointmentUseCase.findByPatientId(appointment.patient().id()))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/patient/" + appointment.patient().id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(appointment.id()));
    }


    @Test
    void shouldReturnAppointmentsByPatientId() throws Exception {
        var appointment = createAppointment();
        Mockito.when(findAppointmentUseCase.findByPatientId(appointment.patient().id()))
            .thenReturn(List.of(appointment));
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(get("/appointments/patient/" + appointment.patient().id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].patient.id").value(appointment.patient().id()));
    }


    @Test
    void shouldUpdateAppointmentSuccessfully() throws Exception {
        var appointment = createAppointment();
        var request = createValidAppointmentRequest();
        Mockito.when(updateAppointmentUseCase.execute(eq(appointment.id()), any(Appointment.class)))
            .thenReturn(appointment);
        Mockito.when(appointmentMapper.toResponse(any(Appointment.class)))
            .thenReturn(createAppointmentResponse(appointment));

        mockMvc.perform(put("/appointments/" + appointment.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(appointment.id()));
    }

    private AppointmentRequest createValidAppointmentRequest() {
        return new AppointmentRequest(
            "patient-id-123",
            "doctor-id-456",
            java.time.LocalDateTime.of(2025, 7, 20, 10, 0),
            "AGENDADO",
            "CONSULTA",
            "Rotina",
            "Nenhuma observação"
        );
    }

    private Appointment createAppointmentFromRequest(AppointmentRequest request) {
        return Appointment.builder()
            .id("appointment-id-789")
            .patient(createPatient())
            .healthcareProfessional(createUser())
            .scheduledDateTime(request.scheduledDateTime())
            .status(request.status())
            .type(request.type())
            .reason(request.reason())
            .notes(request.notes())
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now())
            .build();
    }

    private AppointmentResponse createAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
            appointment.id(),
            appointment.patient(),
            appointment.healthcareProfessional(),
            appointment.scheduledDateTime(),
            appointment.status(),
            appointment.type(),
            appointment.reason(),
            appointment.notes(),
            appointment.createdAt(),
            appointment.updatedAt()
        );
    }

    private Appointment createAppointment() {
        return Appointment.builder()
            .id("appointment-id-789")
            .patient(createPatient())
            .healthcareProfessional(createUser())
            .scheduledDateTime(java.time.LocalDateTime.of(2025, 7, 20, 10, 0))
            .status("AGENDADO")
            .type("CONSULTA")
            .reason("Rotina")
            .notes("Nenhuma observação")
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now())
            .build();
    }

    private com.persou.prontosus.domain.Patient createPatient() {
        return com.persou.prontosus.domain.Patient.builder()
            .id("patient-id-123")
            .fullName("João da Silva")
            .cpf("12345678901")
            .birthDate(java.time.LocalDate.of(1990, 5, 15))
            .gender("MALE")
            .phoneNumber("11987654321")
            .email("joao.silva@email.com")
            .address(null)
            .emergencyContactName("Maria da Silva")
            .emergencyContactPhone("11976543210")
            .knownAllergies("")
            .currentMedications("")
            .chronicConditions("")
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now())
            .build();
    }

    private User createUser() {
        return User.builder()
            .id("doctor-id-456")
            .fullName("Dr. Carlos")
            .username("testuser")
            .professionalDocument("98765432100")
            .specialty("Cardiology")
            .active(true)
            .email("carlos@email.com")
            .role("DOCTOR")
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now())
            .build();
    }
}
