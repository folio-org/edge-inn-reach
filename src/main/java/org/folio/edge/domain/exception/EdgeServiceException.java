package org.folio.edge.domain.exception;

import lombok.Getter;

@Getter
public class EdgeServiceException extends RuntimeException {

  private int status;

  public EdgeServiceException(String message) {
    super(message);
  }

  public EdgeServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public EdgeServiceException(int status, String message) {
    super(message);
    this.status = status;
  }
}
