package com.persou.prontosus.adapters;

import static com.persou.prontosus.config.MessagesErrorException.USER_NOT_AUTHORIZED;
import static org.springframework.http.HttpStatus.OK;

import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.adapters.response.UserResponse;
import com.persou.prontosus.application.AuthenticateUserUseCase;
import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.UserMapper;
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
    private final UserMapper userMapper;

    @PostMapping("/login")
    @ResponseStatus(OK)
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticateUserUseCase.execute(request.username(), request.password())
            .map(userMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_AUTHORIZED));
    }

}