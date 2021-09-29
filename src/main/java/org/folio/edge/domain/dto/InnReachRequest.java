package org.folio.edge.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnReachRequest {
  private URI requestUrl;
  private String requestBody;
}
