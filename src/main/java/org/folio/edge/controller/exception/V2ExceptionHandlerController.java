package org.folio.edge.controller.exception;

import java.util.stream.Collectors;

import org.folio.edge.domain.exception.CirculationProcessException;
import org.folio.edge.domain.exception.EntityNotFoundException;
import org.folio.edge.dto.InnReachResponseDTO;
import org.folio.edge.mapper.InnReachErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "org.folio.edge.controller")
public class V2ExceptionHandlerController {

  private final InnReachErrorMapper mapper;

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public InnReachResponseDTO handleException(Exception e) {
    log.error("Unexpected exception: " + e.getMessage(), e);

    return failed(e);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public InnReachResponseDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.warn("Argument validation failed", e);

    var bindingResult = e.getBindingResult();
    var innReachErrors = bindingResult.getFieldErrors().stream()
      .map(mapper::toInnReachError)
      .collect(Collectors.toList());

    return failed("Argument validation failed")
      .errors(innReachErrors);
  }

  @ExceptionHandler(CirculationProcessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public InnReachResponseDTO handleException(CirculationProcessException e) {
    log.warn("Unsupported circulation operation", e);

    return failed("Unsupported circulation operation");
  }

  @ExceptionHandler({ EntityNotFoundException.class, IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public InnReachResponseDTO handleEntityNotFoundException(Exception e) {
    log.warn(e.getMessage(), e);

    return failed(e);
  }

  private InnReachResponseDTO failed(Exception e) {
    return failed(e.getMessage());
  }

  private InnReachResponseDTO failed(String reason) {
    return new InnReachResponseDTO()
      .status("failed")
      .reason(reason);
  }
}
