package com.github.vas.atanasov.userservice.webapp.models.wrappers;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.FieldError;

@Data
@Builder
public class RestValidationError {
  private final String field;
  private final String fieldErrorCode;
  private final String errorMessage;

  public static RestValidationError fromFiledError(FieldError err) {
    return RestValidationError.builder()
        .field(err.getField())
        .fieldErrorCode(err.getCode())
        .errorMessage(err.getDefaultMessage())
        .build();
  }
}
