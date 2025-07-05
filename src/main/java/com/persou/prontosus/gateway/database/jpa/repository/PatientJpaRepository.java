package com.persou.prontosus.gateway.database.jpa.repository;

import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByCpf(String cpf);

    @Query("SELECT p FROM PatientEntity p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PatientEntity> findByFullNameContainingIgnoreCase(@Param("name") String name);

    boolean existsByCpf(String cpf);

    @Query("SELECT p FROM PatientEntity p WHERE p.phoneNumber = :phone OR p.emergencyContactPhone = :phone")
    List<PatientEntity> findByPhoneNumber(@Param("phone") String phone);
}
