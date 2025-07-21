package com.persou.prontosus.adapters.config;

import com.persou.prontosus.application.RegisterUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.config.security.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class UserControllerMockConfig {

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase() {
        return Mockito.mock(RegisterUserUseCase.class);
    }

    @Bean
    public UserMapper userMapper() {
        return Mockito.mock(UserMapper.class);
    }
}

