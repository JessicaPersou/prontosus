package com.persou.prontosus.config.exceptions;

public record ValidationErrorDetail(
    String field,
    String message
) {

}