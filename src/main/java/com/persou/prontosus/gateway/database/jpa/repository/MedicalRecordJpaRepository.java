package com.persou.prontosus.gateway.database.jpa.repository;

import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalRecordJpaRepository extends JpaRepository<MedicalRecordEntity, String> {
    List<MedicalRecordEntity> findByPatientOrderByConsultationDateDesc(PatientEntity patient);

    List<MedicalRecordEntity> findByHealthcareProfessionalOrderByConsultationDateDesc(
        UserEntity healthcareProfessional);

    @Query("SELECT mr FROM MedicalRecordEntity mr WHERE mr.patient.id = :patientId ORDER BY mr.consultationDate DESC")
    List<MedicalRecordEntity> findByPatientIdOrderByConsultationDateDesc(@Param("patientId") String patientId);

    @Query("SELECT mr FROM MedicalRecordEntity mr WHERE mr.consultationDate BETWEEN :startDate AND :endDate ORDER BY mr.consultationDate DESC")
    List<MedicalRecordEntity> findByConsultationDateBetween(@Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT mr FROM MedicalRecordEntity mr WHERE mr.patient.id = :patientId AND mr.consultationDate BETWEEN :startDate AND :endDate ORDER BY mr.consultationDate DESC")
    List<MedicalRecordEntity> findByPatientAndDateRange(@Param("patientId") String patientId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);
}
