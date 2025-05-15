package org.folio.edge.security.service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.folio.edge.security.SecurityManagerService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import org.folio.edge.api.utils.security.SecureStore;
import org.folio.edge.config.properties.SecurityStoreConfigProperties;
import org.folio.edge.domain.dto.TenantMapping;
import org.folio.edge.security.store.SecureStoreFactory;
import org.folio.edge.security.store.SecureTenantsProducer;
import org.folio.edgecommonspring.domain.entity.ConnectionSystemParameters;

@Log4j2
@Component
@RequiredArgsConstructor
public class SecurityService {

  private static final String TENANT_MAPPINGS_SPLIT_SYMBOL = ",";
  private static final String TENANT_MAPPING_SPLIT_SYMBOL = ":";
  private static final int TENANT_MAPPING_PARTS_SIZE = 2;

  private final SecurityStoreConfigProperties securityStoreConfigProperties;

  private SecureStore secureStore;
  private final Map<String, TenantMapping> tenantMappingMap = new ConcurrentHashMap<>();
  private final SecurityManagerService securityManagerService;

  @PostConstruct
  public void init() {
    log.debug("Starting initialization with securityStoreConfigProperties: [{}]", securityStoreConfigProperties);

    var secureStoreProps = fetchProperties();
    log.debug("Secure store properties have been initialized: [{}]", secureStoreProps);

    this.secureStore = SecureStoreFactory.getSecureStore(securityStoreConfigProperties.getSecureStoreType(), secureStoreProps);
    log.debug("Secure store has been initialized: [{}]", secureStore);

    var tenantsMappings = SecureTenantsProducer.getTenantsMappings(secureStoreProps, secureStore,
        securityStoreConfigProperties.getInnreachTenantsMappings());
    log.debug("Tenant mappings have been initialized: [{}]", tenantsMappings);
    log.info("Tenant mappings have been initialized");
    tenantsMappings.ifPresent(mappings ->
      Arrays.stream(mappings.split(TENANT_MAPPINGS_SPLIT_SYMBOL))
      .map(mapping -> mapping.split(TENANT_MAPPING_SPLIT_SYMBOL))
      .filter(mappingArray -> mappingArray.length == TENANT_MAPPING_PARTS_SIZE)
      .forEach(mappingArray -> tenantMappingMap.put(mappingArray[0], new TenantMapping(mappingArray[0], mappingArray[1], securityStoreConfigProperties.getInnreachClient())))
    );

    log.info("Tenant map has been initialized: [{}]", tenantMappingMap);
  }

  private static Properties fetchProperties(){
    Properties properties = new Properties();
    try {
      Resource resource = new ClassPathResource("ephemeral.properties");
      InputStream in = resource.getInputStream();
      properties.load(in);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    return properties;
  }

  public TenantMapping getTenantMappingByLocalServerKey(UUID localServerKey) {
    log.debug("getTenantMappingByLocalServerKey");
    return Optional.ofNullable(tenantMappingMap.get(localServerKey.toString()))
      .orElseThrow(() -> new BadCredentialsException("Tenant mapping for local server key: " + localServerKey + " not found!"));
  }

  public ConnectionSystemParameters getInnReachConnectionParameters(UUID localServerKey) {
    log.debug("getInnReachConnectionParameters");
    var tenantMapping = getTenantMappingByLocalServerKey(localServerKey);
    return securityManagerService.getParamsDependingOnCachePresent(securityStoreConfigProperties.getInnreachClient(),
      tenantMapping.getTenantId(), tenantMapping.getUsername());
  }

  public ConnectionSystemParameters getOkapiConnectionParameters(String edgeApiKey) {
    log.debug("getOkapiConnectionParameters");
    return securityManagerService.getParamsWithToken(edgeApiKey);
  }

}
