package com.persou.prontosus.config.exceptions;

import org.springframework.http.HttpStatus;


public class BusinessValidationException extends ApiException {

    public BusinessValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, ErrorType.BUSINESS, message);
    }
}