package org.folio.edge.domain.dto.systemuser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Permission {
  private String permissionName;
}
