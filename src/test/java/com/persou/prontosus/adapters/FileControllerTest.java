package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.persou.prontosus.adapters.config.FileControllerMockConfig;
import com.persou.prontosus.adapters.config.TestSecurityConfig;
import com.persou.prontosus.adapters.response.FileAttachmentResponse;
import com.persou.prontosus.application.UploadExamFileUseCase;
import com.persou.prontosus.config.mapper.FileAttachmentMapper;
import com.persou.prontosus.config.security.JwtService;
import com.persou.prontosus.domain.FileAttachment;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
@Import({TestSecurityConfig.class, FileControllerMockConfig.class})
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UploadExamFileUseCase uploadExamFileUseCase;

    @Autowired
    private FileAttachmentMapper fileAttachmentMapper;

    @Autowired
    private JwtService jwtService;



    @Test
    void getPatientFiles_deveRetornarListaDeArquivos() throws Exception {
        FileAttachment attachment = createFileAttachment();
        FileAttachmentResponse response = createFileAttachmentResponse(attachment);

        Mockito.when(uploadExamFileUseCase.getPatientFiles("pat123"))
            .thenReturn(List.of(attachment));
        Mockito.when(fileAttachmentMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(get("/files/patient/" + "pat123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("file-id-456"));
    }

    private FileAttachment createFileAttachment() {
        return FileAttachment.builder()
            .id("file-id-456")
            .fileName("exam.pdf")
            .fileType("application/pdf")
            .fileSize(12345L)
            .filePath("/files/download/file-id-456")
            .build();
    }

    private FileAttachmentResponse createFileAttachmentResponse(FileAttachment attachment) {
        return new FileAttachmentResponse(
            attachment.id(),
            null,
            attachment.fileName(),
            attachment.filePath(),
            attachment.fileType(),
            attachment.fileSize(),
            attachment.fileType(),
            null,
            LocalDateTime.now(),
            null);
    }
}
