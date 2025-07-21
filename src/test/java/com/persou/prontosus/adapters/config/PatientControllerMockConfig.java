package com.persou.prontosus.adapters.config;

import com.persou.prontosus.application.FindPatientUseCase;
import com.persou.prontosus.application.RegisterPatientUseCase;
import com.persou.prontosus.application.UpdatePatientUseCase;
import com.persou.prontosus.config.mapper.PatientMapper;
import com.persou.prontosus.config.security.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class PatientControllerMockConfig {

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public FindPatientUseCase findPatientUseCase() {
        return Mockito.mock(FindPatientUseCase.class);
    }

    @Bean
    public RegisterPatientUseCase registerPatientUseCase() {
        return Mockito.mock(RegisterPatientUseCase.class);
    }

    @Bean
    public UpdatePatientUseCase updatePatientUseCase() {
        return Mockito.mock(UpdatePatientUseCase.class);
    }

    @Bean
    public PatientMapper patientMapper() {
        return Mockito.mock(PatientMapper.class);
    }
}