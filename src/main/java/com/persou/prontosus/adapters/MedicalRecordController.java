package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.request.MedicalRecordRequest;
import com.persou.prontosus.adapters.response.MedicalRecordResponse;
import com.persou.prontosus.application.CreateMedicalRecordUseCase;
import com.persou.prontosus.application.UpdateMedicalRecordUseCase;
import com.persou.prontosus.application.ViewMedicalHistoryUseCase;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.domain.MedicalRecord;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/medical-records")
public class MedicalRecordController {

    private final CreateMedicalRecordUseCase createMedicalRecordUseCase;
    private final UpdateMedicalRecordUseCase updateMedicalRecordUseCase;
    private final ViewMedicalHistoryUseCase viewMedicalHistoryUseCase;
    private final MedicalRecordMapper medicalRecordMapper;

    @GetMapping("/patient/{patientId}")
    @ResponseStatus(OK)
    public List<MedicalRecordResponse> getPatientHistory(@PathVariable String patientId) {
        try {
            log.info("Buscando histórico médico do paciente: {}", patientId);
            List<MedicalRecord> records = viewMedicalHistoryUseCase.getPatientHistory(patientId);
            log.info("Encontrados {} registros para o paciente {}", records.size(), patientId);

            return records.stream()
                .map(record -> {
                    try {
                        return medicalRecordMapper.toResponse(record);
                    } catch (Exception e) {
                        log.error("Erro ao mapear registro {}: {}", record.id(), e.getMessage());
                        throw e;
                    }
                })
                .toList();
        } catch (Exception e) {
            log.error("Erro ao buscar histórico do paciente {}: {}", patientId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/professional/{professionalId}")
    @ResponseStatus(OK)
    public List<MedicalRecordResponse> getProfessionalRecords(@PathVariable String professionalId) {
        try {
            log.info("Buscando registros do profissional: {}", professionalId);
            List<MedicalRecord> records = viewMedicalHistoryUseCase.getProfessionalRecords(professionalId);
            log.info("Encontrados {} registros para o profissional {}", records.size(), professionalId);

            return records.stream()
                .map(medicalRecordMapper::toResponse)
                .toList();
        } catch (Exception e) {
            log.error("Erro ao buscar registros do profissional {}: {}", professionalId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/patient/{patientId}/professional/{professionalId}")
    @ResponseStatus(CREATED)
    public MedicalRecordResponse create(
        @PathVariable String patientId,
        @PathVariable String professionalId,
        @Valid @RequestBody MedicalRecordRequest request) {

        try {
            log.info("Criando registro médico para paciente {} e profissional {}", patientId, professionalId);

            MedicalRecord medicalRecord = MedicalRecord.builder()
                .consultationDate(request.consultationDate() != null ? request.consultationDate() : LocalDateTime.now())
                .chiefComplaint(request.chiefComplaint())
                .historyOfPresentIllness(request.historyOfPresentIllness())
                .physicalExamination(request.physicalExamination())
                .vitalSigns(request.vitalSigns())
                .diagnosis(request.diagnosis())
                .treatment(request.treatment())
                .prescriptions(request.prescriptions())
                .observations(request.observations())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

            MedicalRecord savedRecord = createMedicalRecordUseCase.execute(patientId, professionalId, medicalRecord);
            log.info("Registro médico criado com sucesso: {}", savedRecord.id());

            return medicalRecordMapper.toResponse(savedRecord);
        } catch (Exception e) {
            log.error("Erro ao criar registro médico para paciente {} e profissional {}: {}",
                patientId, professionalId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{recordId}")
    @ResponseStatus(OK)
    public MedicalRecordResponse update(@PathVariable String recordId,
                                        @Valid @RequestBody MedicalRecordRequest request) {
        try {
            log.info("Atualizando registro médico: {}", recordId);

            MedicalRecord updatedRecord = MedicalRecord.builder()
                .chiefComplaint(request.chiefComplaint())
                .historyOfPresentIllness(request.historyOfPresentIllness())
                .physicalExamination(request.physicalExamination())
                .vitalSigns(request.vitalSigns())
                .diagnosis(request.diagnosis())
                .treatment(request.treatment())
                .prescriptions(request.prescriptions())
                .observations(request.observations())
                .build();

            MedicalRecord savedRecord = updateMedicalRecordUseCase.execute(recordId, updatedRecord);
            log.info("Registro médico atualizado com sucesso: {}", savedRecord.id());

            return medicalRecordMapper.toResponse(savedRecord);
        } catch (Exception e) {
            log.error("Erro ao atualizar registro médico {}: {}", recordId, e.getMessage(), e);
            throw e;
        }
    }
}