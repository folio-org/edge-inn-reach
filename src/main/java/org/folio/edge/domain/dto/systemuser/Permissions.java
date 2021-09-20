package org.folio.edge.domain.dto.systemuser;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class Permissions {
  private String id;
  private String userId;
  private List<String> permissions;
}
