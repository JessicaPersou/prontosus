package com.persou.prontosus.adapters.config;

import com.persou.prontosus.application.CreateMedicalRecordUseCase;
import com.persou.prontosus.application.UpdateMedicalRecordUseCase;
import com.persou.prontosus.application.ViewMedicalHistoryUseCase;
import com.persou.prontosus.config.mapper.MedicalRecordMapper;
import com.persou.prontosus.config.security.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MedicalRecordControllerMockConfig {

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public MedicalRecordMapper medicalRecordMapper() {
        return Mockito.mock(MedicalRecordMapper.class);
    }

    @Bean
    public CreateMedicalRecordUseCase createMedicalRecordUseCase() {
        return Mockito.mock(CreateMedicalRecordUseCase.class);
    }

    @Bean
    public UpdateMedicalRecordUseCase updateMedicalRecordUseCase() {
        return Mockito.mock(UpdateMedicalRecordUseCase.class);
    }

    @Bean
    public ViewMedicalHistoryUseCase viewMedicalHistoryUseCase() {
        return Mockito.mock(ViewMedicalHistoryUseCase.class);
    }
}