package com.persou.prontosus.adapters;

import static com.persou.prontosus.config.MessagesErrorException.DOCUMENT_NOT_FOUND;
import static com.persou.prontosus.config.MessagesErrorException.ENTITY_NOT_FOUND;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.adapters.response.PatientResponse;
import com.persou.prontosus.application.FindPatientUseCase;
import com.persou.prontosus.application.RegisterPatientUseCase;
import com.persou.prontosus.application.UpdatePatientUseCase;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.domain.Patient;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final FindPatientUseCase findPatientUseCase;
    private final RegisterPatientUseCase registerPatientUseCase;
    private final UpdatePatientUseCase updatePatientUseCase;
    private final PatientMapper patientMapper;

    @GetMapping
    @ResponseStatus(OK)
    public List<PatientResponse> findAll() {
        return findPatientUseCase.findAll()
            .stream()
            .map(patientMapper::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public PatientResponse findById(@PathVariable String id) {
        var patient = findPatientUseCase.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ENTITY_NOT_FOUND));
        return patientMapper.toResponse(patient);
    }

    @GetMapping("/cpf/{cpf}")
    @ResponseStatus(OK)
    public PatientResponse findByCpf(@PathVariable String cpf) {
        var patient = findPatientUseCase.findByCpf(cpf)
            .orElseThrow(() -> new ResourceNotFoundException(DOCUMENT_NOT_FOUND + ": " + cpf));
        return patientMapper.toResponse(patient);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public List<PatientResponse> findByName(@RequestParam String name) {
        return findPatientUseCase.findByName(name)
            .stream()
            .map(patientMapper::toResponse)
            .toList();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        Patient patient = Patient.builder()
            .cpf(request.cpf())
            .fullName(request.fullName())
            .birthDate(request.birthDate())
            .gender(request.gender())
            .phoneNumber(request.phoneNumber())
            .email(request.email())
            .address(request.address())
            .emergencyContactName(request.emergencyContactName())
            .emergencyContactPhone(request.emergencyContactPhone())
            .knownAllergies(request.knownAllergies())
            .currentMedications(request.currentMedications())
            .chronicConditions(request.chronicConditions())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Patient savedPatient = registerPatientUseCase.execute(patient);
        return patientMapper.toResponse(savedPatient);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public PatientResponse update(@PathVariable String id, @Valid @RequestBody PatientRequest request) {
        Patient updatedPatient = Patient.builder()
            .id(id)
            .cpf(request.cpf())
            .fullName(request.fullName())
            .birthDate(request.birthDate())
            .gender(request.gender())
            .phoneNumber(request.phoneNumber())
            .email(request.email())
            .address(request.address())
            .emergencyContactName(request.emergencyContactName())
            .emergencyContactPhone(request.emergencyContactPhone())
            .knownAllergies(request.knownAllergies())
            .currentMedications(request.currentMedications())
            .chronicConditions(request.chronicConditions())
            .build();

        Patient savedPatient = updatePatientUseCase.execute(id, updatedPatient);
        return patientMapper.toResponse(savedPatient);
    }
}