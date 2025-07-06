package com.persou.prontosus.adapters;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.application.AuthenticateUserUseCase;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return authenticateUserUseCase.execute(request.username(), request.password())
            .map(user -> ResponseEntity.status(OK).body(toUserResponse(user)))
            .orElse(ResponseEntity.status(UNAUTHORIZED).build());
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
            .id(user.id())
            .username(user.username())
            .fullName(user.fullName())
            .email(user.email())
            .professionalDocument(user.professionalDocument())
            .role(user.role())
            .specialty(user.specialty())
            .active(user.active())
            .createdAt(user.createdAt())
            .updatedAt(user.updatedAt())
            .lastLoginAt(user.lastLoginAt())
            .build();
    }
}