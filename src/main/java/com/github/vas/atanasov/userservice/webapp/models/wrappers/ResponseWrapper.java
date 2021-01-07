package com.github.vas.atanasov.userservice.webapp.models.wrappers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.vas.atanasov.userservice.webapp.enums.RestMessage;
import com.github.vas.atanasov.userservice.webapp.enums.RestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "message", "data", "errors"})
public class ResponseWrapper {
  private final RestStatus status;
  private final RestMessage message;
  private final Object data;
  private final Object errors;
}
