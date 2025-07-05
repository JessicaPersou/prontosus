package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.AppointmentMapper;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.gateway.database.jpa.repository.AppointmentJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentJpaRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public List<Appointment> findByPatientOrderByScheduledDateTimeDesc(Patient patient) {
        return List.of();
    }

    @Override
    public List<Appointment> findAppointmentsByProfessionalAndDateRange(
        User healthcareProfessional, LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<Appointment> findAppointmentsByStatusAndDateRange(
        AppointmentStatus status, LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status) {
        return List.of();
    }

    @Override
    public List<Appointment> findByProfessionalAndDate(Long professionalId, LocalDateTime date) {
        return List.of();
    }
}
