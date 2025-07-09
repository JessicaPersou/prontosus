package com.persou.prontosus.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persou.prontosus.adapters.request.UserRequest;
import com.persou.prontosus.application.RegisterUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.mocks.UserMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRequest request = createValidUserRequest();
        User user = UserMock.mockDomain();

        when(registerUserUseCase.execute(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(any());


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        UserRequest request = createValidUserRequest()
            .withUsername("");


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("username"))
            .andExpect(jsonPath("$.details[0].message").value("Nome de usuário é obrigatório"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUsernameIsTooShort() throws Exception {
        UserRequest request = createValidUserRequest()
            .withUsername("ab"); // Menos que 3 caracteres


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("username"))
            .andExpect(jsonPath("$.details[0].message").value("Nome de usuário deve ter entre 3 e 50 caracteres"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUsernameIsTooLong() throws Exception {
        String longUsername = "a".repeat(51); // Mais que 50 caracteres
        UserRequest request = createValidUserRequest()
            .withUsername(longUsername);


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("username"))
            .andExpect(jsonPath("$.details[0].message").value("Nome de usuário deve ter entre 3 e 50 caracteres"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {
        UserRequest request = createValidUserRequest()
            .withPassword("123"); // Menos que 6 caracteres


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("password"))
            .andExpect(jsonPath("$.details[0].message").value("Senha deve ter pelo menos 6 caracteres"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenFullNameIsBlank() throws Exception {
        UserRequest request = createValidUserRequest()
            .withFullName("");


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("fullName"))
            .andExpect(jsonPath("$.details[0].message").value("Nome completo é obrigatório"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenFullNameIsTooShort() throws Exception {
        UserRequest request = createValidUserRequest()
            .withFullName("A"); // Menos que 2 caracteres


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("fullName"))
            .andExpect(jsonPath("$.details[0].message").value("Nome completo deve ter entre 2 e 200 caracteres"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        UserRequest request = createValidUserRequest()
            .withEmail("invalid-email");


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("email"))
            .andExpect(jsonPath("$.details[0].message").value("Email deve ter formato válido"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenProfessionalDocumentIsBlank() throws Exception {
        UserRequest request = createValidUserRequest()
            .withProfessionalDocument("");


        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("ValidationError"))
            .andExpect(jsonPath("$.details[0].field").value("professionalDocument"))
            .andExpect(jsonPath("$.details[0].message").value("Documento profissional é obrigatório"));
    }

}