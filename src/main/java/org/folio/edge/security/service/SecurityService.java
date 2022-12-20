package org.folio.edge.security.service;

import static org.folio.edge.api.utils.Constants.X_OKAPI_TOKEN;
import static org.folio.edge.api.utils.util.PropertiesUtil.getProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import org.folio.edge.api.utils.exception.AuthorizationException;
import org.folio.edge.api.utils.model.ClientInfo;
import org.folio.edge.api.utils.security.SecureStore;
import org.folio.edge.api.utils.util.ApiKeyParser;
import org.folio.edge.client.AuthnClient;
import org.folio.edge.config.properties.SecurityStoreConfigProperties;
import org.folio.edge.domain.dto.TenantMapping;
import org.folio.edge.security.store.SecureStoreFactory;
import org.folio.edge.security.store.SecureTenantsProducer;
import org.folio.edge.utils.CredentialsUtils;
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
  private final AuthnClient authnClient;

  private SecureStore secureStore;
  private final Map<String, TenantMapping> tenantMappingMap = new ConcurrentHashMap<>();

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

  @Cacheable(value = SYSTEM_USER_PARAMETERS_CACHE, key = "#localServerKey")
  public ConnectionSystemParameters getInnReachConnectionParameters(UUID localServerKey) {
    log.debug("getInnReachConnectionParameters");
    var tenantMapping = getTenantMappingByLocalServerKey(localServerKey);

    return enrichConnectionSystemParametersWithOkapiToken(securityStoreConfigProperties.getInnreachClient(),
        tenantMapping.getTenantId(), tenantMapping.getUsername());
  }

  @Cacheable(value = SYSTEM_USER_PARAMETERS_CACHE, key = "#edgeApiKey")
  public ConnectionSystemParameters getOkapiConnectionParameters(String edgeApiKey) {
    try {
      log.debug("getOkapiConnectionParameters");
      ClientInfo clientInfo = CredentialsUtils.parseApiKey(edgeApiKey);
      return enrichConnectionSystemParametersWithOkapiToken(clientInfo.salt, clientInfo.tenantId, clientInfo.username);
    } catch (ApiKeyParser.MalformedApiKeyException e) {
      throw new AuthorizationException("Malformed edge api key: " + edgeApiKey);
    }
  }

  private ConnectionSystemParameters enrichConnectionSystemParametersWithOkapiToken(String salt, String tenantId, String username) {
    try {
      log.debug("enrichConnectionSystemParametersWithOkapiToken");
      return enrichWithOkapiToken(ConnectionSystemParameters.builder()
        .tenantId(tenantId)
        .username(username)
        .password(secureStore.get(salt, tenantId, username))
        .build());
    } catch (SecureStore.NotFoundException e) {
      log.warn("Cannot get system connection properties for: " + tenantId);
      throw new AuthorizationException("Cannot get system connection properties for: " + tenantId);
    }
  }

  private ConnectionSystemParameters enrichWithOkapiToken(ConnectionSystemParameters parameters) {
    log.debug("enrichWithOkapiToken");
    final String token = Optional.ofNullable(authnClient.getApiKey(parameters, parameters.getTenantId())
      .getHeaders()
      .get(X_OKAPI_TOKEN))
      .orElseThrow(() -> new AuthorizationException("Cannot retrieve okapi token"))
      .get(0);

    parameters.setOkapiToken(token);
    log.info("Set okapi token in the header.");
    return parameters;
  }

}
