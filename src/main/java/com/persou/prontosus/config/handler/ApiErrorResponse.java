package com.persou.prontosus.config.handler;

import com.persou.prontosus.config.exceptions.ValidationErrorDetail;
import java.util.List;

public record ApiErrorResponse(
    String type,
    String message,
    List<ValidationErrorDetail> details
) {

}