package com.persou.prontosus.adapters;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.application.AuthenticateUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.config.security.JwtService;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.mocks.UserMock;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        User user = UserMock.mockDomain();
        String token = "jwt.token.here";

        when(authenticateUserUseCase.execute("testuser", "password123"))
            .thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.username()))
            .thenReturn(token);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(token))
            .andExpect(jsonPath("$.type").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(86400L))
            .andExpect(jsonPath("$.user.username").value(user.username()))
            .andExpect(jsonPath("$.user.username").value(user.username()))
            .andExpect(jsonPath("$.user.email").value(user.email()))
            .andExpect(jsonPath("$.user.role").value(user.role()))
            .andExpect(jsonPath("$.user.specialty").value(user.specialty()));
    }

    @Test
    void shouldReturnNotFoundWhenInvalidCredentials() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("wronguser")
            .password("wrongpassword")
            .build();

        when(authenticateUserUseCase.execute("wronguser", "wrongpassword"))
            .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").value("ResourceNotFound"))
            .andExpect(jsonPath("$.message").value("Usuário não autorizado"));
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("")
            .password("password123")
            .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("username"))
            .andExpect(jsonPath("$.details[0].message").value("Nome de usuário é obrigatório"));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsBlank() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("")
            .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("password"))
            .andExpect(jsonPath("$.details[0].message").value("Senha é obrigatória"));
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsNull() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username(null)
            .password("password123")
            .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsNull() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password(null)
            .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"));
    }

    @Test
    void shouldReturnBadRequestWhenBothFieldsAreEmpty() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("")
            .password("")
            .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details").isArray())
            .andExpect(jsonPath("$.details.length()").value(2));
    }

    @Test
    void shouldReturnBadRequestWhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenContentTypeIsNotJson() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnsupportedMediaType());
    }
}