package com.persou.prontosus.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.gateway.PatientRepository;
import com.persou.prontosus.mocks.PatientMock;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FindPatientUseCaseTest {

    private final PatientRepository patientRepository = mock();
    private final PatientMapper patientMapper = mock();
    private final FindPatientUseCase findPatientUseCase = new FindPatientUseCase(patientRepository, patientMapper);

    @Test
    void shouldFindPatientById() {
        var patient = PatientMock.mockDomain();
        when(patientRepository.findById(patient.id())).thenReturn(Optional.of(patient));

        var result = findPatientUseCase.findById(patient.id());

        assertThat(result).isPresent().isNotNull();
        assertThat(result.get().id()).isEqualTo(patient.id());

        verify(patientRepository, times(1)).findById(patient.id());
    }

    @Test
    void shouldFindPatientByCpf() {
        var patient = PatientMock.mockDomain();
        when(patientRepository.findByCpf(patient.cpf())).thenReturn(Optional.of(patient));

        var result = findPatientUseCase.findByCpf(patient.cpf());

        assertThat(result).isPresent().isNotNull();
        assertThat(result.get().cpf()).isEqualTo(patient.cpf());

        verify(patientRepository, times(1)).findByCpf(patient.cpf());
    }

    @Test
    void shouldFindPatientByName() {
        var patients = PatientMock.mockDomainList();
        String name = "Joe";

        when(patientRepository.findByFullNameContainingIgnoreCase(name)).thenReturn(patients);

        var result = patientRepository.findByFullNameContainingIgnoreCase(name);

        assertThat(result).isNotNull().isEqualTo(patients).hasSize(1);

        verify(patientRepository, times(1)).findByFullNameContainingIgnoreCase(name);
    }
}