package org.folio.ed.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.folio.ed.domain.dto.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.folio.ed.client.utils.ErrorHelper.ErrorCode.INTERNAL_SERVER_ERROR;
import static org.folio.ed.client.utils.ErrorHelper.ErrorCode.VALIDATION_ERROR;
import static org.folio.ed.client.utils.ErrorHelper.createExternalError;

@RestControllerAdvice
@Log4j2
public class ExceptionHandlingController {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Errors handleGlobalException(Exception ex) {
    logExceptionMessage(ex);
    return createExternalError(ex.getMessage(), INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
    MissingServletRequestParameterException.class,
    MethodArgumentTypeMismatchException.class,
    HttpMessageNotReadableException.class,
    IllegalArgumentException.class
  })
  public Errors handleValidationErrors(Exception ex) {
    logExceptionMessage(ex);
    return createExternalError(ex.getMessage(), VALIDATION_ERROR);
  }

  /**
   * Exception handler for Feign client errors, passing them back to the edge API caller.
   * Overall flow here: Errors from Feign clients are mapped to {@link Errors}
   * then wrapped into {@link ResponseEntity} object with same status code which got from Feign client
   */
  @ExceptionHandler(FeignException.class)
  public ResponseEntity<Errors> handleFeignError(FeignException ex) {
    logExceptionMessage(ex);
    var status = HttpStatus.resolve(ex.status());
    String body = ex.contentUTF8();
    Errors errors;
    try {
      errors = objectMapper.readValue(body, Errors.class);
      return new ResponseEntity<>(errors, status);
    } catch (JsonProcessingException e) {
      log.warn("Unexpected exception. Can't retrieve response body: {}", e.getMessage());
      errors = createExternalError(ex.getMessage(), INTERNAL_SERVER_ERROR);
      return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void logExceptionMessage(Exception ex) {
    log.warn("Exception occurred ", ex);
  }

}
