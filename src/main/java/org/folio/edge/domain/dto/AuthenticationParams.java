package org.folio.edge.domain.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.folio.edge.validation.InnReachAuthenticationHeader;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationParams {

  @NotEmpty
  @InnReachAuthenticationHeader
  private String authorization;

  private String okapiTenant;
  private String okapiToken;
}
