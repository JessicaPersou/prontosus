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
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public List<MedicalRecordResponse> getPatientHistory(@PathVariable Long patientId) {
        return viewMedicalHistoryUseCase.getPatientHistory(patientId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/professional/{professionalId}")
    @ResponseStatus(OK)
    public List<MedicalRecordResponse> getProfessionalRecords(@PathVariable Long professionalId) {
        return viewMedicalHistoryUseCase.getProfessionalRecords(professionalId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @PostMapping("/patient/{patientId}/professional/{professionalId}")
    @ResponseStatus(CREATED)
    public MedicalRecordResponse create(
        @PathVariable Long patientId,
        @PathVariable Long professionalId,
        @Valid @RequestBody MedicalRecordRequest request) {

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
        return toResponse(savedRecord);
    }

    @PutMapping("/{recordId}")
    @ResponseStatus(OK)
    public MedicalRecordResponse update(@PathVariable Long recordId, @Valid @RequestBody MedicalRecordRequest request) {
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
        return toResponse(savedRecord);
    }

    private MedicalRecordResponse toResponse(MedicalRecord medicalRecord) {
        return MedicalRecordResponse.builder()
            .id(medicalRecord.id())
            .patient(medicalRecord.patient())
            .healthcareProfessional(medicalRecord.healthcareProfessional())
            .appointment(medicalRecord.appointment())
            .consultationDate(medicalRecord.consultationDate())
            .chiefComplaint(medicalRecord.chiefComplaint())
            .historyOfPresentIllness(medicalRecord.historyOfPresentIllness())
            .physicalExamination(medicalRecord.physicalExamination())
            .vitalSigns(medicalRecord.vitalSigns())
            .diagnosis(medicalRecord.diagnosis())
            .treatment(medicalRecord.treatment())
            .prescriptions(medicalRecord.prescriptions())
            .observations(medicalRecord.observations())
            .attachments(medicalRecord.attachments())
            .createdAt(medicalRecord.createdAt())
            .updatedAt(medicalRecord.updatedAt())
            .build();
    }
}
