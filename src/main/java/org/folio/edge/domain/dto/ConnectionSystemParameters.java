package org.folio.edge.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionSystemParameters {

  private String username;
  private String password;

  @JsonIgnore
  private String okapiToken;

  @JsonIgnore
  private String tenantId;
}
