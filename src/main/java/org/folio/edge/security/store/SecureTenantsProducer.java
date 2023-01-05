package org.folio.edge.security.store;

import java.util.Optional;
import java.util.Properties;

import lombok.extern.log4j.Log4j2;
import org.folio.edge.api.utils.security.SecureStore;

@Log4j2
public class SecureTenantsProducer {

  private SecureTenantsProducer() {}

  public static Optional<String> getTenants(Properties secureStoreProps, SecureStore secureStore, String innreachTenants) {
    if (secureStore instanceof TenantAwareAWSParamStore) {
      var stringOptional = ((TenantAwareAWSParamStore) secureStore).getTenants(innreachTenants);
      if (stringOptional.isEmpty()) {
        log.warn("Tenants list not found in AWS Param store. Please create variable, which contains comma separated list of tenants");
      }
      return stringOptional;
    }
    return Optional.of((String) secureStoreProps.get("tenants"));
  }
  public static Optional<String> getTenantsMappings(Properties secureStoreProps, SecureStore secureStore, String innreachTenantsMappings) {
    if (secureStore instanceof TenantAwareAWSParamStore) {
      log.debug("secureStore is instance of TenantAwareAWSParamStore ");
      var stringOptional = ((TenantAwareAWSParamStore) secureStore).getTenantsMappings(innreachTenantsMappings);
      if (stringOptional.isEmpty()) {
        log.warn("Tenants mappings list not found in AWS Param store. Please create variable, which contains comma separated list of tenants mappings");
      }
      log.info("Tenants mapping {}", stringOptional);
      return stringOptional;
    }
    log.info("Tenants mapping found");
    return Optional.of((String) secureStoreProps.get("tenantsMappings"));
  }
}
