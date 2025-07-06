package com.persou.prontosus.gateway.database.jpa.repository;

import com.persou.prontosus.domain.enums.ProfessionalRole;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByProfessionalDocument(String professionalDocument);

    List<UserEntity> findByRoleAndActiveTrue(ProfessionalRole role);

    List<UserEntity> findByActiveTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByProfessionalDocument(String professionalDocument);

    @Query("SELECT u FROM UserEntity u WHERE u.role = :role AND u.active = true AND LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserEntity> findByRoleAndNameContaining(@Param("role") ProfessionalRole role, @Param("name") String name);
}