package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.response.FileAttachmentResponse;
import com.persou.prontosus.application.UploadExamFileUseCase;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.User;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final UploadExamFileUseCase uploadExamFileUseCase;

    @PostMapping("/medical-record/{medicalRecordId}")
    @ResponseStatus(CREATED)
    public FileAttachmentResponse uploadFile(
        @PathVariable Long medicalRecordId,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "description", required = false) String description) throws IOException {

        // Para o MVP, criar um usuário mock - em produção viria do contexto de segurança
        User mockUser = User.builder()
            .id(1L)
            .username("admin")
            .fullName("Administrador")
            .build();

        FileAttachment savedFile = uploadExamFileUseCase.execute(medicalRecordId, file, description, mockUser);
        return toResponse(savedFile);
    }

    @GetMapping("/patient/{patientId}")
    @ResponseStatus(OK)
    public List<FileAttachmentResponse> getPatientFiles(@PathVariable Long patientId) {
        return uploadExamFileUseCase.getPatientFiles(patientId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private FileAttachmentResponse toResponse(FileAttachment fileAttachment) {
        return FileAttachmentResponse.builder()
            .id(fileAttachment.id())
            .medicalRecord(fileAttachment.medicalRecord())
            .fileName(fileAttachment.fileName())
            .filePath(fileAttachment.filePath())
            .contentType(fileAttachment.contentType())
            .fileSize(fileAttachment.fileSize())
            .fileType(fileAttachment.fileType())
            .description(fileAttachment.description())
            .uploadedAt(fileAttachment.uploadedAt())
            .uploadedBy(fileAttachment.uploadedBy())
            .build();
    }
}