package com.persou.prontosus.config.mapper;

import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.ProfessionalRole;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "role", source = "role")
    UserEntity toEntity(User user);

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "role", source = "role")
    User toDomain(UserEntity userEntity);

    @Mapping(target = "role", source = "role")
    UserResponse toResponse(User user);

    default ProfessionalRole mapRole(String role) {
        if (role == null) {
            return null;
        }
        try {
            return ProfessionalRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role inválido: " + role + ". Valores aceitos: DOCTOR, NURSE, ADMIN");
        }
    }

    default String mapRole(ProfessionalRole role) {
        return role != null ? role.name() : null;
    }
}