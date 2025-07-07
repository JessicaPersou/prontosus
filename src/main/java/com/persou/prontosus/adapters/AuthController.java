package com.persou.prontosus.adapters;

import static com.persou.prontosus.config.MessagesErrorException.USER_NOT_AUTHORIZED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.adapters.response.LoginResponse;
import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.application.AuthenticateUserUseCase;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.config.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    @ResponseStatus(OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticateUserUseCase.execute(request.username(), request.password())
            .map(user -> {
                String token = jwtService.generateToken(user.username());

                return LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .expiresIn(86400L) // 24 horas em segundos
                    .user(LoginResponse.UserInfo.builder()
                        .id(user.id())
                        .username(user.username())
                        .fullName(user.fullName())
                        .email(user.email())
                        .role(user.role())
                        .specialty(user.specialty())
                        .build())
                    .build();
            })
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_AUTHORIZED));
    }

}