package com.persou.prontosus.gateway;

import com.persou.prontosus.config.mapper.FileAttachmentMapper;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.FileAttachment;
import com.persou.prontosus.domain.MedicalRecord;
import com.persou.prontosus.domain.enums.FileType;
import com.persou.prontosus.gateway.database.jpa.FileAttachmentEntity;
import com.persou.prontosus.gateway.database.jpa.MedicalRecordEntity;
import com.persou.prontosus.gateway.database.jpa.repository.FileAttachmentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileAttachmentRepositoryImplTest {

    @Mock
    private FileAttachmentJpaRepository fileAttachmentJpaRepository;

    @Mock
    private FileAttachmentMapper fileAttachmentMapper;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    private FileAttachmentRepositoryImpl fileAttachmentRepository;

    @BeforeEach
    void setUp() {
        fileAttachmentRepository = new FileAttachmentRepositoryImpl(
            fileAttachmentJpaRepository, fileAttachmentMapper, medicalRecordMapper);
    }

    @Test
    void shouldSaveFileAttachment() {
        FileAttachment fileAttachment = FileAttachment.builder()
            .id("file1")
            .fileName("test.pdf")
            .filePath("/uploads/test.pdf")
            .contentType("application/pdf")
            .fileSize(1024L)
            .fileType("EXAM_RESULT")
            .uploadedAt(LocalDateTime.now())
            .build();

        FileAttachmentEntity entity = FileAttachmentEntity.builder()
            .id("file1")
            .fileName("test.pdf")
            .filePath("/uploads/test.pdf")
            .contentType("application/pdf")
            .fileSize(1024L)
            .fileType(FileType.EXAM_RESULT)
            .uploadedAt(fileAttachment.uploadedAt())
            .build();

        FileAttachmentEntity savedEntity = entity.toBuilder().build();

        when(fileAttachmentMapper.toEntity(fileAttachment)).thenReturn(entity);
        when(fileAttachmentJpaRepository.save(entity)).thenReturn(savedEntity);
        when(fileAttachmentMapper.toDomain(savedEntity)).thenReturn(fileAttachment);

        FileAttachment result = fileAttachmentRepository.save(fileAttachment);

        assertNotNull(result);
        assertEquals(fileAttachment.id(), result.id());
        assertEquals(fileAttachment.fileName(), result.fileName());

        verify(fileAttachmentMapper).toEntity(fileAttachment);
        verify(fileAttachmentJpaRepository).save(entity);
        verify(fileAttachmentMapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindFileAttachmentsByMedicalRecordOrderByUploadedAt() {
        MedicalRecord medicalRecord = MedicalRecord.builder()
            .id("record1")
            .chiefComplaint("Test complaint")
            .build();

        MedicalRecordEntity medicalRecordEntity = MedicalRecordEntity.builder()
            .id("record1")
            .chiefComplaint("Test complaint")
            .build();

        List<FileAttachmentEntity> entities = List.of(
            FileAttachmentEntity.builder()
                .id("file1")
                .fileName("recent.pdf")
                .uploadedAt(LocalDateTime.now())
                .medicalRecord(medicalRecordEntity)
                .build(),
            FileAttachmentEntity.builder()
                .id("file2")
                .fileName("older.pdf")
                .uploadedAt(LocalDateTime.now().minusDays(1))
                .medicalRecord(medicalRecordEntity)
                .build()
        );

        List<FileAttachment> fileAttachments = List.of(
            FileAttachment.builder().id("file1").fileName("recent.pdf").build(),
            FileAttachment.builder().id("file2").fileName("older.pdf").build()
        );

        when(medicalRecordMapper.toEntity(medicalRecord)).thenReturn(medicalRecordEntity);
        when(fileAttachmentJpaRepository.findByMedicalRecordOrderByUploadedAtDesc(medicalRecordEntity))
            .thenReturn(entities);
        when(fileAttachmentMapper.toDomain(entities.get(0))).thenReturn(fileAttachments.get(0));
        when(fileAttachmentMapper.toDomain(entities.get(1))).thenReturn(fileAttachments.get(1));

        List<FileAttachment> result = fileAttachmentRepository.findByMedicalRecordOrderByUploadedAtDesc(medicalRecord);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1", result.get(0).id());
        assertEquals("file2", result.get(1).id());

        verify(medicalRecordMapper).toEntity(medicalRecord);
        verify(fileAttachmentJpaRepository).findByMedicalRecordOrderByUploadedAtDesc(medicalRecordEntity);
        verify(fileAttachmentMapper, times(2)).toDomain(any(FileAttachmentEntity.class));
    }

    @Test
    void shouldFindFileAttachmentsByFileType() {
        FileType fileType = FileType.EXAM_RESULT;

        List<FileAttachmentEntity> entities = List.of(
            FileAttachmentEntity.builder()
                .id("file1")
                .fileName("exam1.pdf")
                .fileType(fileType)
                .build(),
            FileAttachmentEntity.builder()
                .id("file2")
                .fileName("exam2.pdf")
                .fileType(fileType)
                .build()
        );

        List<FileAttachment> fileAttachments = List.of(
            FileAttachment.builder().id("file1").fileName("exam1.pdf").fileType("EXAM_RESULT").build(),
            FileAttachment.builder().id("file2").fileName("exam2.pdf").fileType("EXAM_RESULT").build()
        );

        when(fileAttachmentJpaRepository.findByFileType(fileType)).thenReturn(entities);
        when(fileAttachmentMapper.toDomain(entities.get(0))).thenReturn(fileAttachments.get(0));
        when(fileAttachmentMapper.toDomain(entities.get(1))).thenReturn(fileAttachments.get(1));

        List<FileAttachment> result = fileAttachmentRepository.findByFileType(fileType);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("EXAM_RESULT", result.get(0).fileType());
        assertEquals("EXAM_RESULT", result.get(1).fileType());

        verify(fileAttachmentJpaRepository).findByFileType(fileType);
        verify(fileAttachmentMapper, times(2)).toDomain(any(FileAttachmentEntity.class));
    }

    @Test
    void shouldFindFileAttachmentsByPatientId() {
        String patientId = "patient1";

        List<FileAttachmentEntity> entities = List.of(
            FileAttachmentEntity.builder()
                .id("file1")
                .fileName("patient_file1.pdf")
                .build(),
            FileAttachmentEntity.builder()
                .id("file2")
                .fileName("patient_file2.pdf")
                .build()
        );

        List<FileAttachment> fileAttachments = List.of(
            FileAttachment.builder().id("file1").fileName("patient_file1.pdf").build(),
            FileAttachment.builder().id("file2").fileName("patient_file2.pdf").build()
        );

        when(fileAttachmentJpaRepository.findByPatientId(patientId)).thenReturn(entities);
        when(fileAttachmentMapper.toDomain(entities.get(0))).thenReturn(fileAttachments.get(0));
        when(fileAttachmentMapper.toDomain(entities.get(1))).thenReturn(fileAttachments.get(1));

        List<FileAttachment> result = fileAttachmentRepository.findByPatientId(patientId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("file1", result.get(0).id());
        assertEquals("file2", result.get(1).id());

        verify(fileAttachmentJpaRepository).findByPatientId(patientId);
        verify(fileAttachmentMapper, times(2)).toDomain(any(FileAttachmentEntity.class));
    }

    @Test
    void shouldFindFileAttachmentsByPatientIdAndFileType() {
        String patientId = "patient1";
        FileType fileType = FileType.IMAGE;

        List<FileAttachmentEntity> entities = List.of(
            FileAttachmentEntity.builder()
                .id("file1")
                .fileName("image1.jpg")
                .fileType(fileType)
                .build()
        );

        List<FileAttachment> fileAttachments = List.of(
            FileAttachment.builder().id("file1").fileName("image1.jpg").fileType("IMAGE").build()
        );

        when(fileAttachmentJpaRepository.findByPatientIdAndFileType(patientId, fileType))
            .thenReturn(entities);
        when(fileAttachmentMapper.toDomain(entities.get(0))).thenReturn(fileAttachments.get(0));

        List<FileAttachment> result = fileAttachmentRepository.findByPatientIdAndFileType(patientId, fileType);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IMAGE", result.get(0).fileType());
        assertEquals("image1.jpg", result.get(0).fileName());

        verify(fileAttachmentJpaRepository).findByPatientIdAndFileType(patientId, fileType);
        verify(fileAttachmentMapper).toDomain(entities.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoFilesFound() {
        String patientId = "patient_without_files";

        when(fileAttachmentJpaRepository.findByPatientId(patientId)).thenReturn(List.of());

        List<FileAttachment> result = fileAttachmentRepository.findByPatientId(patientId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(fileAttachmentJpaRepository).findByPatientId(patientId);
        verify(fileAttachmentMapper, never()).toDomain(any());
    }

    @Test
    void shouldReturnEmptyListWhenNoFilesFoundByType() {
        FileType fileType = FileType.PRESCRIPTION;

        when(fileAttachmentJpaRepository.findByFileType(fileType)).thenReturn(List.of());

        List<FileAttachment> result = fileAttachmentRepository.findByFileType(fileType);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(fileAttachmentJpaRepository).findByFileType(fileType);
        verify(fileAttachmentMapper, never()).toDomain(any());
    }

    @Test
    void shouldHandleDifferentFileTypes() {
        List<FileType> fileTypes = List.of(FileType.EXAM_RESULT, FileType.IMAGE, FileType.MEDICAL_REPORT);

        for (FileType fileType : fileTypes) {
            List<FileAttachmentEntity> entities = List.of(
                FileAttachmentEntity.builder()
                    .id("file_" + fileType.name())
                    .fileName("test." + fileType.name().toLowerCase())
                    .fileType(fileType)
                    .build()
            );

            List<FileAttachment> fileAttachments = List.of(
                FileAttachment.builder()
                    .id("file_" + fileType.name())
                    .fileType(fileType.name())
                    .build()
            );

            when(fileAttachmentJpaRepository.findByFileType(fileType)).thenReturn(entities);
            when(fileAttachmentMapper.toDomain(entities.get(0))).thenReturn(fileAttachments.get(0));

            List<FileAttachment> result = fileAttachmentRepository.findByFileType(fileType);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(fileType.name(), result.get(0).fileType());
        }

        verify(fileAttachmentJpaRepository, times(3)).findByFileType(any(FileType.class));
    }
}