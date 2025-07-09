package com.persou.prontosus.application;

import com.persou.prontosus.config.exceptions.BusinessValidationException;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.PatientEntity;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import com.persou.prontosus.gateway.database.jpa.repository.FileAttachmentJpaRepository;
import com.persou.prontosus.gateway.database.jpa.repository.MedicalRecordJpaRepository;
import com.persou.prontosus.gateway.database.jpa.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadExamFileUseCaseTest {

    @Mock
    private FileAttachmentJpaRepository fileAttachmentJpaRepository;

    @Mock
    private MedicalRecordJpaRepository medicalRecordJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private MultipartFile multipartFile;

    private UploadExamFileUseCase uploadExamFileUseCase;

    @BeforeEach
    void setUp() {
        uploadExamFileUseCase = new UploadExamFileUseCase(
            fileAttachmentJpaRepository, medicalRecordJpaRepository, userJpaRepository);
        ReflectionTestUtils.setField(uploadExamFileUseCase, "uploadDir", "test-uploads");
    }

    @Test
    void shouldUploadFileSuccessfully() throws IOException {
        String medicalRecordId = "record1";
        String description = "Test file";
        User uploadedBy = User.builder()
            .id("user1")
            .username("testuser")
            .fullName("Test User")
            .build();

        PatientEntity patientEntity = PatientEntity.builder()
            .id("patient1")
            .fullName("Test Patient")
            .build();

        MedicalRecordEntity medicalRecordEntity = MedicalRecordEntity.builder()
            .id(medicalRecordId)
            .patient(patientEntity)
            .build();

        UserEntity userEntity = UserEntity.builder()
            .id("user1")
            .username("testuser")
            .fullName("Test User")
            .build();

        FileAttachmentEntity savedEntity = FileAttachmentEntity.builder()
            .id("file1")
            .medicalRecord(medicalRecordEntity)
            .fileName("test.pdf")
            .contentType("application/pdf")
            .fileSize(1024L)
            .fileType(FileType.EXAM_RESULT)
            .description(description)
            .uploadedAt(LocalDateTime.now())
            .uploadedBy(userEntity)
            .build();

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));

        when(medicalRecordJpaRepository.findById(medicalRecordId)).thenReturn(Optional.of(medicalRecordEntity));
        when(userJpaRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));
        when(fileAttachmentJpaRepository.save(any(FileAttachmentEntity.class))).thenReturn(savedEntity);

        FileAttachment result = uploadExamFileUseCase.execute(medicalRecordId, multipartFile, description, uploadedBy);

        assertNotNull(result);
        assertEquals("file1", result.id());
        assertEquals("test.pdf", result.fileName());
        assertEquals("application/pdf", result.contentType());
        assertEquals(1024L, result.fileSize());
        assertEquals("EXAM_RESULT", result.fileType());
        assertEquals(description, result.description());

        verify(medicalRecordJpaRepository).findById(medicalRecordId);
        verify(userJpaRepository).findByUsername("testuser");
        verify(fileAttachmentJpaRepository).save(any(FileAttachmentEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        String medicalRecordId = "record1";
        User uploadedBy = User.builder().username("testuser").build();

        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(BusinessValidationException.class,
            () -> uploadExamFileUseCase.execute(medicalRecordId, multipartFile, null, uploadedBy));

        verify(medicalRecordJpaRepository, never()).findById(anyString());
        verify(fileAttachmentJpaRepository, never()).save(any());
    }

    @Test
    void shouldHandleFileWithoutOriginalName() throws IOException {
        String medicalRecordId = "record1";
        User uploadedBy = User.builder()
            .username("testuser")
            .build();

        MedicalRecordEntity medicalRecordEntity = MedicalRecordEntity.builder()
            .id(medicalRecordId)
            .patient(PatientEntity.builder().fullName("Test Patient").build())
            .build();

        UserEntity userEntity = UserEntity.builder()
            .username("testuser")
            .fullName("Test User")
            .build();

        FileAttachmentEntity savedEntity = FileAttachmentEntity.builder()
            .id("file1")
            .fileName("arquivo_sem_nome")
            .fileType(FileType.OTHER)
            .build();

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        when(medicalRecordJpaRepository.findById(medicalRecordId)).thenReturn(Optional.of(medicalRecordEntity));
        when(userJpaRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));
        when(fileAttachmentJpaRepository.save(any(FileAttachmentEntity.class))).thenReturn(savedEntity);

        FileAttachment result = uploadExamFileUseCase.execute(medicalRecordId, multipartFile, null, uploadedBy);

        assertNotNull(result);
        verify(fileAttachmentJpaRepository).save(argThat(entity ->
            "arquivo_sem_nome".equals(entity.getFileName())));
    }

    @Test
    void shouldGetPatientFiles() {
        String patientId = "patient1";

        List<FileAttachmentEntity> entities = List.of(
            FileAttachmentEntity.builder()
                .id("file1")
                .fileName("test1.pdf")
                .fileType(FileType.EXAM_RESULT)
                .uploadedAt(LocalDateTime.now())
                .build(),
            FileAttachmentEntity.builder()
                .id("file2")
                .fileName("test2.jpg")
                .fileType(FileType.IMAGE)
                .uploadedAt(LocalDateTime.now().minusDays(1))
                .build()
        );

        when(fileAttachmentJpaRepository.findByPatientId(patientId)).thenReturn(entities);

        List<FileAttachment> result = uploadExamFileUseCase.getPatientFiles(patientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1", result.get(0).id());
        assertEquals("file2", result.get(1).id());
        assertEquals("test1.pdf", result.get(0).fileName());
        assertEquals("test2.jpg", result.get(1).fileName());

        verify(fileAttachmentJpaRepository).findByPatientId(patientId);
    }

    @Test
    void shouldReturnEmptyListWhenNoFilesFound() {
        String patientId = "patient1";

        when(fileAttachmentJpaRepository.findByPatientId(patientId)).thenReturn(List.of());

        List<FileAttachment> result = uploadExamFileUseCase.getPatientFiles(patientId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(fileAttachmentJpaRepository).findByPatientId(patientId);
    }

    @Test
    void shouldHandleExceptionWhenGettingPatientFiles() {
        String patientId = "patient1";

        when(fileAttachmentJpaRepository.findByPatientId(patientId))
            .thenThrow(new RuntimeException("Database error"));

        List<FileAttachment> result = uploadExamFileUseCase.getPatientFiles(patientId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(fileAttachmentJpaRepository).findByPatientId(patientId);
    }
}