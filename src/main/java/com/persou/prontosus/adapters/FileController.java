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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final UploadExamFileUseCase uploadExamFileUseCase;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final UserRepository userRepository;

    @PostMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<FileAttachmentResponse> uploadFile(
        @PathVariable String medicalRecordId,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "description", required = false) String description) {

        try {
            log.info("Recebendo upload de arquivo para registro médico: {}", medicalRecordId);
            log.info("Arquivo: {}, Tamanho: {} bytes", file.getOriginalFilename(), file.getSize());

            // Validar autenticação
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                log.error("Usuário não autenticado");
                return ResponseEntity.status(401).build();
            }

            log.info("Usuário autenticado: {}", auth.getName());

            // Buscar usuário
            User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", auth.getName());
                    return new RuntimeException("Usuário não encontrado: " + auth.getName());
                });

            log.info("Usuário encontrado: {} - {}", currentUser.username(), currentUser.fullName());

            // Validar arquivo
            if (file.isEmpty()) {
                log.error("Arquivo vazio");
                return ResponseEntity.badRequest().build();
            }

            // Executar upload
            FileAttachment savedFile = uploadExamFileUseCase.execute(medicalRecordId, file, description, currentUser);

            log.info("Upload concluído com sucesso. ID do arquivo: {}", savedFile.id());

            FileAttachmentResponse response = fileAttachmentMapper.toResponse(savedFile);
            return ResponseEntity.status(CREATED).body(response);

        } catch (IOException e) {
            log.error("Erro de I/O no upload do arquivo: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        } catch (Exception e) {
            log.error("Erro geral no upload do arquivo: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/patient/{patientId}")
    @ResponseStatus(OK)
    public List<FileAttachmentResponse> getPatientFiles(@PathVariable String patientId) {
        try {
            log.info("Buscando arquivos do paciente: {}", patientId);

            List<FileAttachmentResponse> files = uploadExamFileUseCase.getPatientFiles(patientId)
                .stream()
                .map(fileAttachmentMapper::toResponse)
                .toList();

            log.info("Retornando {} arquivos para o paciente {}", files.size(), patientId);
            return files;

        } catch (Exception e) {
            log.error("Erro ao buscar arquivos do paciente {}: {}", patientId, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar arquivos: " + e.getMessage(), e);
        }
    }
}