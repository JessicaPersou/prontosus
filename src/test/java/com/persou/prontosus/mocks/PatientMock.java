package com.persou.prontosus.mocks;

import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PatientMock {

    public static Patient mockDomain() {
        return Patient.builder()
            .id(UUID.randomUUID().toString())
            .cpf("12345678901")
            .fullName("John Doe")
            .birthDate(LocalDate.of(1990, 1, 1))
            .email("joe@email.com")
            .build();
    }

    public static PatientEntity mockEntity() {
        return PatientEntity.builder()
            .id(UUID.randomUUID().toString())
            .cpf("12345678901")
            .fullName("John Doe")
            .birthDate(LocalDate.of(1990, 1, 1))
            .email("joe@email.com")
            .build();
    }

    public static List<PatientEntity> mockEntityList() {
        return List.of(mockEntity());
    }

    public static List<Patient> mockDomainList() {
        return List.of(mockDomain());
    }
}
