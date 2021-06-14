package org.folio.edge.dto;

import lombok.Data;

@Data
public class ErrorDto {

  private final int httpCode;
  private final String message;
}
