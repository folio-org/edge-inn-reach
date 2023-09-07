package org.folio.edge.security.service;

import static org.folio.edge.api.utils.util.PropertiesUtil.getProperties;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.edgecommonspring.security.SecurityManagerService;
import org.folio.spring.config.properties.FolioEnvironment;
import org.springframework.beans.factory.annotation.Value;
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

  public static final String SYSTEM_USER_PARAMETERS_CACHE = "systemUserParameters";

  private static final String TENANT_MAPPINGS_SPLIT_SYMBOL = ",";
  private static final String TENANT_MAPPING_SPLIT_SYMBOL = ":";
  private static final int TENANT_MAPPING_PARTS_SIZE = 2;

  private final SecurityStoreConfigProperties securityStoreConfigProperties;

  private SecureStore secureStore;
  private final Map<String, TenantMapping> tenantMappingMap = new ConcurrentHashMap<>();
  private final SecurityManagerService securityManagerService;


  @Value("${okapi_url}")
  private String okapiUrl;

  @Value("${folio.okapi_url}")
  private String folioOkapiUrl;

  private final FolioEnvironment folioEnvironment;


  @PostConstruct
  public void init() {
    log.debug("Starting initialization with securityStoreConfigProperties: [{}]", securityStoreConfigProperties);

    var secureStoreProps = getProperties(securityStoreConfigProperties.getSecureStorePropsFile());
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
    log.info("folio environment okapi url {} , {} , {}", folioEnvironment.getOkapiUrl(), folioOkapiUrl, okapiUrl);
    return securityManagerService.getParamsWithToken(edgeApiKey);
  }

}
