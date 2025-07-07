package com.persou.prontosus.config.mapper;

import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.gateway.database.jpa.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PatientMapper.class, UserMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "medicalRecord", ignore = true)
    Appointment toDomain(AppointmentEntity entity);

    @Mapping(target = "medicalRecord", ignore = true)
    AppointmentEntity toEntity(Appointment domain);
}