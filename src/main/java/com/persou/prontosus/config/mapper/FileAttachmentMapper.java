package com.persou.prontosus.config.mapper;

import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileAttachmentMapper {

    FileAttachmentEntity toEntity(FileAttachment fileAttachment);

    FileAttachment toDomain(FileAttachmentEntity fileAttachment);
}
