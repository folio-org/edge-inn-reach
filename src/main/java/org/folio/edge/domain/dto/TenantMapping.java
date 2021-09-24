package org.folio.edge.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantMapping {
  private String xCodeFrom;
  private String tenantId;
  private String username;
}
