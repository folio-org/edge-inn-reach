package org.folio.edge.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnBean(
  name = {"tenantOkapiHeaderValidationFilter"}
)
public class TenantOkapiHeaderValidationFilterConfig {

  @Bean
  public List<String> tenantOkapiHeaderValidationFilterExcludeUrls() {
    return List.of("/_/");
  }
}
