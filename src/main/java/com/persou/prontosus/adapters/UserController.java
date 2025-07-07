package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.CREATED;

import com.persou.prontosus.adapters.request.UserRequest;
import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.application.RegisterUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.User;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public UserResponse registerUser(@Valid @RequestBody UserRequest request) {
        User user = User.builder()
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

        User savedUser = registerUserUseCase.execute(user);
        return userMapper.toResponse(savedUser);
    }
}