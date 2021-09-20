package org.folio.edge.domain.dto.systemuser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

  private String id;
  private String username;
  private String barcode;
  private boolean active;
  private Personal personal;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Personal {
    private String lastName;
  }
}
