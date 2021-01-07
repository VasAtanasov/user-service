package com.github.vas.atanasov.userservice.webapp.api;

import com.github.vas.atanasov.userservice.webapp.enums.RestMessage;
import com.github.vas.atanasov.userservice.webapp.models.wrappers.ResponseWrapper;
import com.github.vas.atanasov.userservice.webapp.models.wrappers.RestValidationError;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.vas.atanasov.userservice.webapp.enums.RestStatus.FAILURE;
import static com.github.vas.atanasov.userservice.webapp.enums.RestStatus.SUCCESS;

public interface BaseController {

  int DEFAULT_PAGE_SIZE = 15;
  String SORT = "createdDateTime";

  default List<RestValidationError> mapErrors(List<FieldError> fieldErrors) {
    return fieldErrors.stream()
        .map(RestValidationError::fromFiledError)
        .collect(Collectors.toList());
  }

  default ResponseWrapper failureResponse(RestMessage message, Object data) {
    return ResponseWrapper.builder().status(FAILURE).message(message).errors(data).build();
  }

  default ResponseWrapper failureResponse(RestMessage message) {
    return ResponseWrapper.builder().status(FAILURE).message(message).build();
  }

  default ResponseWrapper successResponse(RestMessage message, Object data) {
    return ResponseWrapper.builder().status(SUCCESS).message(message).data(data).build();
  }
}
