package com.persou.prontosus.config.mapper;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);
}
