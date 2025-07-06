package com.persou.prontosus.config.handler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.persou.prontosus.config.exceptions.ApiException;
import com.persou.prontosus.config.exceptions.ErrorType;
import com.persou.prontosus.config.exceptions.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

//    private KoinLogger logger = KoinLogger.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(com.persou.prontosus.config.exceptions.ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
            ex.getType().getDescription(),
            ex.getMessage(),
            ex.getDetails()
        );

        if (ex.getType() == ErrorType.INTERNAL_SERVER_ERROR) {
            logError(ex);
        } else {
//            logger.info("Error on request! " + ex.getMessage());
        }

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        ApiErrorResponse response = new ApiErrorResponse(
            ErrorType.INTERNAL_SERVER_ERROR.getDescription(),
            "An unexpected error occurred. Please try again later.",
            null
        );

        logError(ex);

        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception ex) {
//        logger.error("Unexpected error! " + ex.getMessage(), ex.getCause());
    }

}
