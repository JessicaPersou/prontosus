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
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.FileAttachmentRepository;
import com.persou.prontosus.gateway.MedicalRecordRepository;
import jakarta.xml.bind.ValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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
    private final MedicalRecordRepository medicalRecordRepository;

    @Value("${app.file.upload.dir:uploads}")
    private String uploadDir;

    public FileAttachment execute(String medicalRecordId, MultipartFile file, String description, User uploadedBy)
        throws IOException {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
            .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NOT_FOUND));

        validateFile(file);

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = saveFile(file, fileName);

        FileAttachment attachment = FileAttachment.builder()
            .medicalRecord(medicalRecord)
            .fileName(file.getOriginalFilename())
            .filePath(filePath)
            .contentType(file.getContentType())
            .fileSize(file.getSize())
            .fileType(determineFileType(file.getContentType()).toString())
            .description(description)
            .uploadedAt(LocalDateTime.now())
            .uploadedBy(uploadedBy)
            .build();

        return fileAttachmentRepository.save(attachment);
    }

    public List<FileAttachment> getPatientFiles(String patientId) {
        return fileAttachmentRepository.findByPatientId(patientId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResourceNotFoundException(ENTITY_NOT_FOUND);
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessValidationException(FILE_LARGER_THAN_10MB);
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new BusinessValidationException(FILE_NOT_SUPPORTED);


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