package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.config.AuthControllerMockConfig;
import com.persou.prontosus.adapters.config.TestSecurityConfig;
import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.application.AuthenticateUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.config.security.JwtService;
import com.persou.prontosus.domain.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class, AuthControllerMockConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "testpassword");
        User user = createUser();
        String token = "mocked-jwt-token";

        Mockito.when(authenticateUserUseCase.execute(anyString(), anyString())).thenReturn(Optional.of(user));
        Mockito.when(jwtService.generateToken(user.username())).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(token))
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.user.id").value(user.id()))
            .andExpect(jsonPath("$.user.username").value(user.username()))
            .andExpect(jsonPath("$.user.email").value(user.email()));
    }

    private User createUser() {
        return User.builder()
            .id("user-id-123")
            .username("testuser")
            .password("testpassword")
            .fullName("João da Silva")
            .email("joao@email.com")
            .role("DOCTOR")
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}

