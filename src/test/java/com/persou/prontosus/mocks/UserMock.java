package com.persou.prontosus.mocks;

import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.ProfessionalRole;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserMock {

    public static User mockDomain() {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username("testuser")
            .password("$2a$10$encoded.password")
            .fullName("Dr. João Silva")
            .email("joao.silva@test.com")
            .professionalDocument("CRM123456")
            .role("DOCTOR")
            .specialty("Cardiologia")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static UserEntity mockEntity() {
        return UserEntity.builder()
            .id(UUID.randomUUID().toString())
            .username("testuser")
            .password("$2a$10$encoded.password")
            .fullName("Dr. João Silva")
            .email("joao.silva@test.com")
            .professionalDocument("CRM123456")
            .role(ProfessionalRole.DOCTOR)
            .specialty("Cardiologia")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static List<UserEntity> mockEntityList() {
        return List.of(mockEntity());
    }

    public static List<User> mockDomainList() {
        return List.of(mockDomain());
    }

    public static User mockDomainDoctor() {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username("drmaria")
            .password("$2a$10$encoded.password")
            .fullName("Dra. Maria Santos")
            .email("maria.santos@test.com")
            .professionalDocument("CRM789012")
            .role("DOCTOR")
            .specialty("Pediatria")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static User mockDomainNurse() {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username("enfana")
            .password("$2a$10$encoded.password")
            .fullName("Ana Costa")
            .email("ana.costa@test.com")
            .professionalDocument("COREN345678")
            .role("NURSE")
            .specialty("Enfermagem Geral")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    public static User mockDomainAdmin() {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .username("admin")
            .password("$2a$10$encoded.password")
            .fullName("Administrador Sistema")
            .email("admin@test.com")
            .professionalDocument("ADMIN001")
            .role("ADMIN")
            .specialty(null)
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}