package org.folio.edge.controller.exception;

import jakarta.validation.ConstraintViolationException;

import org.folio.edge.domain.exception.EdgeServiceException;
import org.folio.edge.dto.Error;
import org.folio.edge.dto.InnReachResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class ExceptionHandlerController {

  private static final String INVALID_REQUEST_ERROR_TYPE = "invalid_request";
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Error handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    return new Error()
      .error(INVALID_REQUEST_ERROR_TYPE)
      .errorDescription(e.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Error handleConstraintValidationException(ConstraintViolationException e) {
    return new Error()
      .error(INVALID_REQUEST_ERROR_TYPE)
      .errorDescription(e.getMessage());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Error handleMissingRequestParameterException(MissingServletRequestParameterException e) {
    return new Error()
      .error(INVALID_REQUEST_ERROR_TYPE)
      .errorDescription(String.format("The %s parameter is required.", e.getParameterName()));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Error handleMissingHeaderException(MissingRequestHeaderException e) {
    return new Error()
      .error(INVALID_REQUEST_ERROR_TYPE)
      .errorDescription(String.format("The %s header is required.", e.getHeaderName()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Error handleBadCredentialsException(BadCredentialsException e) {
    return new Error()
      .error("invalid_token")
      .errorDescription("Token authentication failed");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Error handleException(Exception e) {
    log.error("Unexpected exception: " + e.getMessage(), e);

    return new Error()
      .error("internal_error")
      .errorDescription("Internal server error");
  }

  @ExceptionHandler(EdgeServiceException.class)
  public ResponseEntity<Error> handleEdgeServiceException(EdgeServiceException e) {
    return new ResponseEntity<>(new Error().error(INVALID_REQUEST_ERROR_TYPE), HttpStatus.valueOf(e.getStatus()));
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<InnReachResponseDTO> handleFeignStatusException(FeignException feignException) {
    log.error("Unexpected exception: {}", feignException.getMessage());

    var status = HttpStatus.resolve(feignException.status());
    String body = feignException.contentUTF8();
    InnReachResponseDTO innReachResponseDTO = null;
    try {
      innReachResponseDTO = objectMapper.readValue(body, InnReachResponseDTO.class);
    } catch (JsonProcessingException e) {
      log.warn("Unexpected exception. Can't retrieve response body: {}", e.getMessage());
      innReachResponseDTO = failed(body);
    }

    return new ResponseEntity<>(innReachResponseDTO, status);
  }

  private InnReachResponseDTO failed(String reason) {
    return new InnReachResponseDTO()
      .status("failed")
      .reason(reason);
  }
}
