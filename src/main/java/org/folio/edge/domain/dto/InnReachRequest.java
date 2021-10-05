package org.folio.edge.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnReachRequest {
  private URI requestUrl;
  private String requestBody;
  private Map<String, String> headers;
}
