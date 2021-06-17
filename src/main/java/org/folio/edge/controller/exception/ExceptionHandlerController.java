package org.folio.edge.controller.exception;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.folio.edge.dto.Error;

@RestControllerAdvice
public class ExceptionHandlerController {

  private static final String INVALID_REQUEST_ERROR_TYPE = "invalid_request";

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
  public Error handleBadCredentialsException(BadCredentialsException e){
    return new Error()
      .error("invalid_token")
      .errorDescription("Token authentication failed");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Error handleException(Exception e) {
    return new Error()
      .error("internal_error")
      .errorDescription("Internal server error");
  }

}
