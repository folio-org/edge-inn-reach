package org.folio.edge.domain.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
