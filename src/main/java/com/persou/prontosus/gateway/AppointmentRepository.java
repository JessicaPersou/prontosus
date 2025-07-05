package com.persou.prontosus.gateway;

import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository {
    List<Appointment> findByPatientOrderByScheduledDateTimeDesc(Patient patient);

    List<Appointment> findAppointmentsByProfessionalAndDateRange(
        User healthcareProfessional, LocalDateTime start, LocalDateTime end);

    List<Appointment> findAppointmentsByStatusAndDateRange(
        AppointmentStatus status, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    List<Appointment> findByProfessionalAndDate(Long professionalId, LocalDateTime date);
}
