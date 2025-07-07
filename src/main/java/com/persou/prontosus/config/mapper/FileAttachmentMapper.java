package com.persou.prontosus.config.mapper;

import com.persou.prontosus.adapters.response.FileAttachmentResponse;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface FileAttachmentMapper {

    @Mapping(target = "medicalRecord", ignore = true)
    FileAttachmentEntity toEntity(FileAttachment fileAttachment);

    @Mapping(target = "medicalRecord", ignore = true)
    FileAttachment toDomain(FileAttachmentEntity fileAttachment);

    FileAttachmentResponse toResponse(FileAttachment fileAttachment);

    default FileType mapFileType(String fileType) {
        if (fileType == null) {
            return null;
        }
        return FileType.valueOf(fileType);
    }

    default String mapFileType(FileType fileType) {
        return fileType != null ? fileType.name() : null;
    }
}