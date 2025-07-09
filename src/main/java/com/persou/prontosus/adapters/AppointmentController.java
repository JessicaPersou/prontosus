package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.request.AppointmentRequest;
import com.persou.prontosus.adapters.response.AppointmentResponse;
import com.persou.prontosus.application.CreateAppointmentUseCase;
import com.persou.prontosus.application.FindAppointmentUseCase;
import com.persou.prontosus.application.UpdateAppointmentUseCase;
import com.persou.prontosus.config.mapper.AppointmentMapper;
import com.persou.prontosus.domain.Appointment;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final FindAppointmentUseCase findAppointmentUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final AppointmentMapper appointmentMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request) {
        try {
            log.info("Criando agendamento para paciente {} e profissional {}",
                request.patientId(), request.healthcareProfessionalId());

            Appointment appointment = Appointment.builder()
                .scheduledDateTime(request.scheduledDateTime())
                .status(request.status())
                .type(request.type())
                .reason(request.reason())
                .notes(request.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            Appointment savedAppointment = createAppointmentUseCase.execute(
                request.patientId(),
                request.healthcareProfessionalId(),
                appointment
            );

            log.info("Agendamento criado com sucesso: {}", savedAppointment.id());
            return appointmentMapper.toResponse(savedAppointment);

        } catch (Exception e) {
            log.error("Erro ao criar agendamento: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/patient/{patientId}")
    @ResponseStatus(OK)
    public List<AppointmentResponse> getPatientAppointments(@PathVariable String patientId) {
        try {
            log.info("Buscando agendamentos do paciente: {}", patientId);

            List<Appointment> appointments = findAppointmentUseCase.findByPatientId(patientId);
            log.info("Encontrados {} agendamentos para o paciente {}", appointments.size(), patientId);

            return appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos do paciente {}: {}", patientId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/professional/{professionalId}")
    @ResponseStatus(OK)
    public List<AppointmentResponse> getProfessionalAppointments(
        @PathVariable String professionalId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        try {
            log.info("Buscando agendamentos do profissional: {}", professionalId);

            List<Appointment> appointments;
            if (start != null && end != null) {
                appointments = findAppointmentUseCase.findByProfessionalAndDateRange(professionalId, start, end);
            } else {
                appointments = findAppointmentUseCase.findByProfessionalId(professionalId);
            }

            log.info("Encontrados {} agendamentos para o profissional {}", appointments.size(), professionalId);

            return appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos do profissional {}: {}", professionalId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/professional/{professionalId}/date/{date}")
    @ResponseStatus(OK)
    public List<AppointmentResponse> getProfessionalAppointmentsByDate(
        @PathVariable String professionalId,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {

        try {
            log.info("Buscando agendamentos do profissional {} para a data {}", professionalId, date);

            List<Appointment> appointments = findAppointmentUseCase.findByProfessionalAndDate(professionalId, date);
            log.info("Encontrados {} agendamentos", appointments.size());

            return appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos por data: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/patient/{patientId}/status/{status}")
    @ResponseStatus(OK)
    public List<AppointmentResponse> getPatientAppointmentsByStatus(
        @PathVariable String patientId,
        @PathVariable String status) {

        try {
            log.info("Buscando agendamentos do paciente {} com status {}", patientId, status);

            List<Appointment> appointments = findAppointmentUseCase.findByPatientIdAndStatus(patientId, status);
            log.info("Encontrados {} agendamentos", appointments.size());

            return appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos por status: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/status/{status}")
    @ResponseStatus(OK)
    public List<AppointmentResponse> getAppointmentsByStatusAndDateRange(
        @PathVariable String status,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        try {
            log.info("Buscando agendamentos com status {} entre {} e {}", status, start, end);

            List<Appointment> appointments = findAppointmentUseCase.findByStatusAndDateRange(status, start, end);
            log.info("Encontrados {} agendamentos", appointments.size());

            return appointments.stream()
                .map(appointmentMapper::toResponse)
                .toList();

        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos por status e período: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{appointmentId}")
    @ResponseStatus(OK)
    public AppointmentResponse update(
        @PathVariable String appointmentId,
        @Valid @RequestBody AppointmentRequest request) {

        try {
            log.info("Atualizando agendamento: {}", appointmentId);

            Appointment updatedAppointment = Appointment.builder()
                .scheduledDateTime(request.scheduledDateTime())
                .status(request.status())
                .type(request.type())
                .reason(request.reason())
                .notes(request.notes())
                .build();

            Appointment savedAppointment = updateAppointmentUseCase.execute(appointmentId, updatedAppointment);
            log.info("Agendamento atualizado com sucesso: {}", savedAppointment.id());

            return appointmentMapper.toResponse(savedAppointment);

        } catch (Exception e) {
            log.error("Erro ao atualizar agendamento {}: {}", appointmentId, e.getMessage(), e);
            throw e;
        }
    }
}