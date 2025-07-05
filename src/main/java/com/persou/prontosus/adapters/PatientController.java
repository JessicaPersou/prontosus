package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.response.PatientResponse;
import com.persou.prontosus.application.FindPatientUseCase;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.PatientMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final FindPatientUseCase findPatientUseCase;
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
    public PatientResponse findById(@PathVariable Long id) {
        var response = findPatientUseCase.findById(id);
        return patientMapper.toResponse(response.orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado")));    }
}
