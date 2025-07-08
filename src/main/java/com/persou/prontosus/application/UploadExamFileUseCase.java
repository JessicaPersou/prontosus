package com.persou.prontosus.application;

import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;
import static com.persou.prontosus.config.MessagesErrorException.FILE_LARGER_THAN_10MB;
import static com.persou.prontosus.config.MessagesErrorException.FILE_NOT_SUPPORTED;
import static com.persou.prontosus.domain.enums.FileType.EXAM_RESULT;
import static com.persou.prontosus.domain.enums.FileType.IMAGE;
import static com.persou.prontosus.domain.enums.FileType.MEDICAL_REPORT;
import static com.persou.prontosus.domain.enums.FileType.OTHER;

import com.persou.prontosus.config.exceptions.BusinessValidationException;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import com.persou.prontosus.gateway.database.jpa.repository.FileAttachmentJpaRepository;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import com.persou.prontosus.gateway.database.jpa.repository.UserJpaRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadExamFileUseCase {

    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;
    private final MedicalRecordJpaRepository medicalRecordJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Value("${app.file.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public FileAttachment execute(String medicalRecordId, MultipartFile file, String description, User uploadedBy)
        throws IOException {

        try {
            log.info("Iniciando upload de arquivo para registro médico: {}", medicalRecordId);
            log.info("Arquivo: {}, Tamanho: {} bytes", file.getOriginalFilename(), file.getSize());
            log.info("Usuário que está fazendo upload: {}", uploadedBy.username());

            if (file.isEmpty()) {
                throw new BusinessValidationException("Arquivo não pode estar vazio");
            }

            var medicalRecordEntity = medicalRecordJpaRepository.findById(medicalRecordId)
                .orElseThrow(() -> {
                    log.error("Registro médico não encontrado: {}", medicalRecordId);
                    return new ResourceNotFoundException(ENTITY_NOT_FOUND + " - Registro médico: " + medicalRecordId);
                });

            log.info("Registro médico encontrado: {}, Paciente: {}",
                medicalRecordEntity.getId(),
                medicalRecordEntity.getPatient().getFullName());

            validateFile(file);

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                originalFileName = "arquivo_sem_nome";
            }

            String fileName = generateUniqueFileName(originalFileName);
            String filePath = saveFile(file, fileName);

            log.info("Arquivo salvo em: {}", filePath);

            var userEntity = userJpaRepository.findByUsername(uploadedBy.username())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", uploadedBy.username());
                    return new ResourceNotFoundException("Usuário não encontrado: " + uploadedBy.username());
                });

            log.info("Usuário encontrado: {}", userEntity.getFullName());

            FileAttachmentEntity entity = FileAttachmentEntity.builder()
                .medicalRecord(medicalRecordEntity)
                .fileName(originalFileName)
                .filePath(filePath)
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .fileSize(file.getSize())
                .fileType(determineFileType(file.getContentType()))
                .description(description != null ? description : "")
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(userEntity)
                .build();

            log.info("Salvando entity do arquivo...");
            FileAttachmentEntity savedEntity = fileAttachmentJpaRepository.save(entity);
            log.info("Arquivo salvo com sucesso: {}", savedEntity.getId());

            return FileAttachment.builder()
                .id(savedEntity.getId())
                .fileName(savedEntity.getFileName())
                .filePath(savedEntity.getFilePath())
                .contentType(savedEntity.getContentType())
                .fileSize(savedEntity.getFileSize())
                .fileType(savedEntity.getFileType().name())
                .description(savedEntity.getDescription())
                .uploadedAt(savedEntity.getUploadedAt())
                .build();

        } catch (IOException e) {
            log.error("Erro de I/O no upload do arquivo: {}", e.getMessage(), e);
            throw e;
        } catch (BusinessValidationException | ResourceNotFoundException e) {
            log.error("Erro de validação no upload: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Erro geral no upload do arquivo para registro {}: {}", medicalRecordId, e.getMessage(), e);
            throw new RuntimeException("Erro interno no upload: " + e.getMessage(), e);
        }
    }

    public List<FileAttachment> getPatientFiles(String patientId) {
        try {
            log.info("Buscando arquivos do paciente: {}", patientId);
            List<FileAttachment> files = fileAttachmentJpaRepository.findByPatientId(patientId)
                .stream()
                .map(entity -> FileAttachment.builder()
                    .id(entity.getId())
                    .fileName(entity.getFileName())
                    .filePath(entity.getFilePath())
                    .contentType(entity.getContentType())
                    .fileSize(entity.getFileSize())
                    .fileType(entity.getFileType().name())
                    .description(entity.getDescription())
                    .uploadedAt(entity.getUploadedAt())
                    .build())
                .toList();
            log.info("Encontrados {} arquivos para o paciente {}", files.size(), patientId);
            return files;
        } catch (Exception e) {
            log.error("Erro ao buscar arquivos do paciente {}: {}", patientId, e.getMessage(), e);
            return List.of();
        }
    }

    private void validateFile(MultipartFile file) {
        log.debug("Validando arquivo...");

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new BusinessValidationException(FILE_LARGER_THAN_10MB);
        }

        String contentType = file.getContentType();
        if (contentType != null && !isAllowedContentType(contentType)) {
            throw new BusinessValidationException(FILE_NOT_SUPPORTED + ": " + contentType);
        }

        log.debug("Arquivo validado com sucesso");
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType.startsWith("image/") ||
            contentType.equals("application/pdf") ||
            contentType.startsWith("text/") ||
            contentType.contains("document") ||
            contentType.contains("spreadsheet") ||
            contentType.equals("application/octet-stream");
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
                extension = originalFileName.substring(dotIndex);
            }
        }
        return UUID.randomUUID() + extension;
    }

    private String saveFile(MultipartFile file, String fileName) throws IOException {
        log.debug("Salvando arquivo fisicamente: {}", fileName);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            log.info("Criando diretório de upload: {}", uploadPath);
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.debug("Arquivo salvo em: {}", filePath);
        return filePath.toString();
    }

    private FileType determineFileType(String contentType) {
        if (contentType == null) {
            return OTHER;
        }

        if (contentType.startsWith("image/")) {
            return IMAGE;
        } else if (contentType.equals("application/pdf")) {
            return EXAM_RESULT;
        } else if (contentType.contains("document")) {
            return MEDICAL_REPORT;
        }
        return OTHER;
    }
}