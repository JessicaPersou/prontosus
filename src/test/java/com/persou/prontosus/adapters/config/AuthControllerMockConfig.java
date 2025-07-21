package com.persou.prontosus.adapters.config;

import com.persou.prontosus.application.AuthenticateUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.config.security.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthControllerMockConfig {

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase() {
        return Mockito.mock(AuthenticateUserUseCase.class);
    }

    @Bean
    public UserMapper userMapper() {
        return Mockito.mock(UserMapper.class);
    }
}
