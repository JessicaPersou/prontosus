package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.config.TestSecurityConfig;
import com.persou.prontosus.adapters.config.UserControllerMockConfig;
import com.persou.prontosus.adapters.request.UserRequest;
import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.application.RegisterUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({TestSecurityConfig.class, UserControllerMockConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private UserMapper userMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRequest request = createValidUserRequest();
        User savedUser = createUserFromRequest(request);
        UserResponse expectedResponse = createUserResponse(savedUser);

        Mockito.when(registerUserUseCase.execute(any(User.class))).thenReturn(savedUser);
        Mockito.when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(savedUser.id()))
            .andExpect(jsonPath("$.username").value(savedUser.username()))
            .andExpect(jsonPath("$.email").value(savedUser.email()));
    }

    private UserRequest createValidUserRequest() {
        return new UserRequest(
            "iduser",
            "testuser",
            "testpassword",
            "João da Silva",
            "joao@email.com",
            "12345678900",
            "DOCTOR",
            "Cardiology",
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    private User createUserFromRequest(UserRequest request) {
        return User.builder()
            .id("user-id-123")
            .username(request.username())
            .password(request.password())
            .fullName(request.fullName())
            .email(request.email())
            .professionalDocument(request.professionalDocument())
            .role(request.role())
            .specialty(request.specialty())
            .active(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private UserResponse createUserResponse(User user) {
        return new UserResponse(
            user.id(),
            user.username(),
            user.fullName(),
            user.email(),
            user.professionalDocument(),
            user.role(),
            user.specialty(),
            user.active(),
            user.createdAt(),
            user.updatedAt(),
            user.lastLoginAt() != null ? user.lastLoginAt() : LocalDateTime.now()
        );
    }
}

