package com.persou.prontosus.config.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.persou.prontosus.config.exceptions.ApiException;
import com.persou.prontosus.config.exceptions.ErrorType;
import com.persou.prontosus.config.exceptions.ValidationErrorDetail;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        log.info("API Exception: {} - {}", ex.getType(), ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
            ex.getType().getDescription(),
            ex.getMessage(),
            ex.getDetails()
        );

        if (ex.getType() == ErrorType.INTERNAL_SERVER_ERROR) {
            log.error("Internal server error: {}", ex.getMessage(), ex);
        }

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("Validation error: {}", ex.getMessage());

        List<ValidationErrorDetail> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ValidationErrorDetail(error.getField(), error.getDefaultMessage()))
            .toList();

        ApiErrorResponse response = new ApiErrorResponse(
            ErrorType.VALIDATION.getDescription(),
            "Erro de validação nos dados enviados",
            details
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info("Illegal argument: {}", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
            ErrorType.BUSINESS.getDescription(),
            ex.getMessage(),
            null
        );

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ApiErrorResponse response = new ApiErrorResponse(
            ErrorType.INTERNAL_SERVER_ERROR.getDescription(),
            "Ocorreu um erro inesperado. Tente novamente mais tarde.",
            null
        );

        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }
}