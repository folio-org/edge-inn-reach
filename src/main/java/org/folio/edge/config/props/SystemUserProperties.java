package org.folio.edge.config.props;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("system-user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserProperties {
  private String username;
  private String password;
  private String lastname;
  private String permissionsFilePath;
  private String okapiTenant;
  private String okapiToken;
  private String okapiUrl;
}
