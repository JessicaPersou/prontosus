package com.persou.prontosus.adapters.config;

import com.persou.prontosus.application.CreateAppointmentUseCase;
import com.persou.prontosus.application.FindAppointmentUseCase;
import com.persou.prontosus.application.UpdateAppointmentUseCase;
import com.persou.prontosus.config.mapper.AppointmentMapper;
import com.persou.prontosus.config.security.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AppointmentControllerMockConfig {

    @Bean
    public CreateAppointmentUseCase createAppointmentUseCase() {
        return Mockito.mock(CreateAppointmentUseCase.class);
    }

    @Bean
    public FindAppointmentUseCase findAppointmentUseCase() {
        return Mockito.mock(FindAppointmentUseCase.class);
    }

    @Bean
    public UpdateAppointmentUseCase updateAppointmentUseCase() {
        return Mockito.mock(UpdateAppointmentUseCase.class);
    }

    @Bean
    public AppointmentMapper appointmentMapper() {
        return Mockito.mock(AppointmentMapper.class);
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

}
