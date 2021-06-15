package org.folio.edge.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.folio.edge.external.validation.InnReachAuthenticationHeader;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InnReachHeadersHolder {

  @NotEmpty
  @Size(max = 5)
  private String xFromCode;

  @Positive
  private Integer xRequestCreationTime;

  @NotEmpty
  @Size(max = 5)
  private String xToCode;

  @NotEmpty
  @InnReachAuthenticationHeader
  private String authorization;
}
