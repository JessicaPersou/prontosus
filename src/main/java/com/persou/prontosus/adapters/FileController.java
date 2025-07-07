package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.response.FileAttachmentResponse;
import com.persou.prontosus.application.UploadExamFileUseCase;
import com.persou.prontosus.config.mapper.FileAttachmentMapper;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.gateway.UserRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FileAttachmentMapper fileAttachmentMapper;
    private final UserRepository userRepository;

    @PostMapping("/medical-record/{medicalRecordId}")
    @ResponseStatus(CREATED)
    public FileAttachmentResponse uploadFile(
        @PathVariable String medicalRecordId,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "description", required = false) String description) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        FileAttachment savedFile = uploadExamFileUseCase.execute(medicalRecordId, file, description, currentUser);
        return fileAttachmentMapper.toResponse(savedFile);
    }

    @GetMapping("/patient/{patientId}")
    @ResponseStatus(OK)
    public List<FileAttachmentResponse> getPatientFiles(@PathVariable String patientId) {
        return uploadExamFileUseCase.getPatientFiles(patientId)
            .stream()
            .map(fileAttachmentMapper::toResponse)
            .toList();
    }
}