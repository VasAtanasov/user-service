package com.github.vas.atanasov.userservice.webapp.api;

import com.github.vas.atanasov.userservice.webapp.enums.RestMessage;
import com.github.vas.atanasov.userservice.webapp.models.wrappers.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalErrorController extends ResponseEntityExceptionHandler
    implements BaseController {

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<Object> handleAll(final Throwable ex) {
    log.error("Exception caught", ex);
    ResponseWrapper response = failureResponse(RestMessage.SOMETHING_WENT_WRONG);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
