package org.folio.edge.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class SecurityStoreConfigProperties {

  @Value("${secure_store}")
  private String secureStoreType;

  @Value("${secure_store_props}")
  private String secureStorePropsFile;

  @Value("${innreach_tenants}")
  private String innreachTenants;

  @Value("${innreach_tenants_mappings}")
  private String innreachTenantsMappings;

  @Value("${innreach_client}")
  private String innreachClient;
}
