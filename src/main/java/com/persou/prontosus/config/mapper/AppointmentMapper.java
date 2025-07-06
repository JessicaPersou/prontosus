package com.persou.prontosus.config.mapper;
import com.persou.prontosus.domain.Appointment;
import com.persou.prontosus.gateway.database.jpa.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentMapper {
    Appointment toDomain(AppointmentEntity entity);

    AppointmentEntity toEntity(Appointment domain);
}
