package com.persou.prontosus.application;

import static com.persou.prontosus.domain.enums.FileType.EXAM_RESULT;
import static com.persou.prontosus.domain.enums.FileType.IMAGE;
import static com.persou.prontosus.domain.enums.FileType.MEDICAL_REPORT;
import static com.persou.prontosus.domain.enums.FileType.OTHER;

import com.persou.prontosus.config.mapper.FileAttachmentMapper;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.FileAttachmentRepository;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadExamFileUseCase {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final MedicalRecordJpaRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Value("${app.file.upload.dir:uploads}")
    private String uploadDir;

    public FileAttachment execute(Long medicalRecordId, MultipartFile file, String description, User uploadedBy)
        throws IOException {
        var medicalRecord = medicalRecordRepository.findById(medicalRecordId)
            .orElseThrow(() -> new IllegalArgumentException("Registro médico não encontrado"));

        validateFile(file);

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = saveFile(file, fileName);

//        return fileAttachmentRepository.save(attachment);
        return null;
    }

    public List<FileAttachment> getPatientFiles(Long patientId) {
        return fileAttachmentRepository.findByPatientId(patientId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Arquivo não pode ser maior que 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType.startsWith("image/") ||
            contentType.equals("application/pdf") ||
            contentType.startsWith("text/") ||
            contentType.contains("document") ||
            contentType.contains("spreadsheet");
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    private FileType determineFileType(String contentType) {
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