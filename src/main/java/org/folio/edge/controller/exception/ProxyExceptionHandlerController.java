package org.folio.edge.controller.exception;

import java.util.Optional;

import org.folio.edge.dto.InnReachResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class ProxyExceptionHandlerController {

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<InnReachResponseDTO> handleFeignStatusException(FeignException feignException) {
    HttpStatus status = Optional.ofNullable(HttpStatus.resolve(feignException.status()))
      .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    InnReachResponseDTO innReachResponseDTO = new InnReachResponseDTO().status(status.toString()).reason(feignException.getMessage());
    return new ResponseEntity<InnReachResponseDTO>(innReachResponseDTO, status);
  }

}
