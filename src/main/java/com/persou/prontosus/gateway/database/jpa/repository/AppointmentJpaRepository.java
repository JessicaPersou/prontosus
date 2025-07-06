package com.persou.prontosus.gateway.database.jpa.repository;

import com.persou.prontosus.domain.enums.AppointmentStatus;
import com.persou.prontosus.gateway.database.jpa.AppointmentEntity;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, String> {
    List<AppointmentEntity> findByPatientOrderByScheduledDateTimeDesc(PatientEntity patient);

    List<AppointmentEntity> findByHealthcareProfessionalAndScheduledDateTimeBetweenOrderByScheduledDateTime(
        UserEntity healthcareProfessional, LocalDateTime start, LocalDateTime end);

    List<AppointmentEntity> findByStatusAndScheduledDateTimeBetween(
        AppointmentStatus status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.patient.id = :patientId AND a.status = :status ORDER BY a.scheduledDateTime DESC")
    List<AppointmentEntity> findByPatientIdAndStatus(@Param("patientId") String patientId, @Param("status")
    AppointmentStatus status);

    @Query("SELECT a FROM AppointmentEntity a WHERE a.healthcareProfessional.id = :professionalId AND DATE(a.scheduledDateTime) = DATE(:date) ORDER BY a.scheduledDateTime")
    List<AppointmentEntity> findByProfessionalAndDate(@Param("professionalId") String professionalId,
                                                      @Param("date") LocalDateTime date);
}