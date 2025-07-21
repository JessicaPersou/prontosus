package com.persou.prontosus.adapters.config;

import com.persou.prontosus.application.UploadExamFileUseCase;
import com.persou.prontosus.config.mapper.FileAttachmentMapper;
import com.persou.prontosus.config.security.JwtService;
import com.persou.prontosus.gateway.UserRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FileControllerMockConfig {

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }
    @Bean
    public UploadExamFileUseCase uploadExamFileUseCase() {
        return Mockito.mock(UploadExamFileUseCase.class);
    }
    @Bean
    public FileAttachmentMapper fileAttachmentMapper() {
        return Mockito.mock(FileAttachmentMapper.class);
    }
    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }}
