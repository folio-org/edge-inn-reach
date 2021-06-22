package org.folio.edge.domain.exception;

public class EdgeServiceException extends RuntimeException {

  public EdgeServiceException(String message) {
    super(message);
  }

  public EdgeServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
