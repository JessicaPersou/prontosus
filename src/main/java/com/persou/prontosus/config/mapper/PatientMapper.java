package com.persou.prontosus.config.mapper;

import com.persou.prontosus.adapters.response.PatientResponse;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.domain.enums.Gender;
import com.persou.prontosus.domain.valueobject.Address;
import com.persou.prontosus.gateway.database.jpa.AddressEntity;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientMapper {

    PatientResponse toResponse(Patient patient);

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    Patient toDomain(PatientEntity patientEntity);

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    PatientEntity toEntity(Patient patient);

    @Mapping(target = "id", source = "updatedPatient.id")
    @Mapping(target = "cpf", source = "updatedPatient.cpf")
    @Mapping(target = "fullName", source = "updatedPatient.fullName")
    @Mapping(target = "birthDate", source = "updatedPatient.birthDate")
    @Mapping(target = "gender", source = "updatedPatient.gender")
    @Mapping(target = "phoneNumber", source = "updatedPatient.phoneNumber")
    @Mapping(target = "email", source = "updatedPatient.email")
    @Mapping(target = "address", source = "updatedPatient.address")
    @Mapping(target = "emergencyContactName", source = "updatedPatient.emergencyContactName")
    @Mapping(target = "emergencyContactPhone", source = "updatedPatient.emergencyContactPhone")
    @Mapping(target = "knownAllergies", source = "updatedPatient.knownAllergies")
    @Mapping(target = "currentMedications", source = "updatedPatient.currentMedications")
    @Mapping(target = "chronicConditions", source = "updatedPatient.chronicConditions")
    @Mapping(target = "medicalRecords", source = "existingPatient.medicalRecords")
    @Mapping(target = "appointments", source = "existingPatient.appointments")
    @Mapping(target = "createdAt", source = "existingPatient.createdAt")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Patient updateEntityFromDomain(Patient updatedPatient, Patient existingPatient);

    // Mapeamento direto do Address
    default Address map(AddressEntity addressEntity) {
        if (addressEntity == null) {
            return null;
        }
        return new Address(
            addressEntity.getZipCode(),
            addressEntity.getStreet(),
            addressEntity.getNumber(),
            addressEntity.getComplement(),
            addressEntity.getNeighborhood(),
            addressEntity.getCity(),
            addressEntity.getState()
        );
    }

    default AddressEntity map(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressEntity(
            address.zipCode(),
            address.street(),
            address.number(),
            address.complement(),
            address.neighborhood(),
            address.city(),
            address.state()
        );
    }

    default Gender mapGender(String gender) {
        if (gender == null) {
            return null;
        }
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gênero inválido: " + gender);
        }
    }

    default String mapGender(Gender gender) {
        return gender != null ? gender.name() : null;
    }
}