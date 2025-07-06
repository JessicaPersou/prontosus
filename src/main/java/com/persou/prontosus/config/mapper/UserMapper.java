package com.persou.prontosus.config.mapper;

import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    UserEntity toEntity(User user);

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    User toDomain(UserEntity userEntity);

    UserResponse toResponse(User user);
}
