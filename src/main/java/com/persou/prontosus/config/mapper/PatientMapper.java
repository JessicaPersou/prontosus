package com.persou.prontosus.config.mapper;
import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.adapters.response.PatientResponse;
import com.persou.prontosus.domain.Patient;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatientMapper {
    PatientResponse toResponse(Patient patient);

    Patient toDomain(PatientResponse patientResponse);

    PatientEntity toEntity(Patient patient);

    Patient toDomain(PatientEntity patientEntity);

    PatientRequest toRequest(Patient patient);

    Patient toDomain(PatientRequest patientRequest);

    Patient updateEntityFromDomain(Patient updatedPatient, Patient existingPatient);
}
