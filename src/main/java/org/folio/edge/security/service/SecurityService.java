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
    log.info("Starting initialization with securityStoreConfigProperties: [{}]", securityStoreConfigProperties);

    var secureStoreProps = getProperties(securityStoreConfigProperties.getSecureStorePropsFile());
    log.info("Secure store properties have been initialized: [{}]", secureStoreProps);

    this.secureStore = SecureStoreFactory.getSecureStore(securityStoreConfigProperties.getSecureStoreType(), secureStoreProps);
    log.info("Secure store has been initialized: [{}]", secureStore);

    var tenantsMappings = SecureTenantsProducer.getTenantsMappings(secureStoreProps, secureStore,
        securityStoreConfigProperties.getInnreachTenantsMappings());
    log.info("Tenant mappings have been initialized: [{}]", tenantsMappings);

    tenantsMappings.ifPresent(mappings ->
      Arrays.stream(mappings.split(TENANT_MAPPINGS_SPLIT_SYMBOL))
      .map(mapping -> mapping.split(TENANT_MAPPING_SPLIT_SYMBOL))
      .filter(mappingArray -> mappingArray.length == TENANT_MAPPING_PARTS_SIZE)
      .forEach(mappingArray -> tenantMappingMap.put(mappingArray[0], new TenantMapping(mappingArray[0], mappingArray[1], securityStoreConfigProperties.getInnreachClient())))
    );

    log.info("Tenant map has been initialized: [{}]", tenantMappingMap);
  }

  public TenantMapping getTenantMappingByLocalServerKey(UUID localServerKey) {
    return Optional.ofNullable(tenantMappingMap.get(localServerKey.toString()))
      .orElseThrow(() -> new BadCredentialsException("Tenant mapping for local server key: " + localServerKey + " not found!"));
  }

  @Cacheable(value = SYSTEM_USER_PARAMETERS_CACHE, key = "#localServerKey")
  public ConnectionSystemParameters getInnReachConnectionParameters(UUID localServerKey) {
    var tenantMapping = getTenantMappingByLocalServerKey(localServerKey);

    return enrichConnectionSystemParametersWithOkapiToken(securityStoreConfigProperties.getInnreachClient(),
        tenantMapping.getTenantId(), tenantMapping.getUsername());
  }

  @Cacheable(value = SYSTEM_USER_PARAMETERS_CACHE, key = "#edgeApiKey")
  public ConnectionSystemParameters getOkapiConnectionParameters(String edgeApiKey) {
    try {
      ClientInfo clientInfo = CredentialsUtils.parseApiKey(edgeApiKey);
      return enrichConnectionSystemParametersWithOkapiToken(clientInfo.salt, clientInfo.tenantId, clientInfo.username);
    } catch (ApiKeyParser.MalformedApiKeyException e) {
      throw new AuthorizationException("Malformed edge api key: " + edgeApiKey);
    }
  }

  private ConnectionSystemParameters enrichConnectionSystemParametersWithOkapiToken(String salt, String tenantId, String username) {
    try {
      log.info("Salt, tenantID, username " + salt + " ---> " + tenantId +" ----> " + username);
      return enrichWithOkapiToken(ConnectionSystemParameters.builder()
        .tenantId(tenantId)
        .username(username)
        .password(secureStore.get(salt, tenantId, username))
        .build());
    } catch (SecureStore.NotFoundException e) {
      throw new AuthorizationException("Cannot get system connection properties for: " + tenantId);
    }
  }

  private ConnectionSystemParameters enrichWithOkapiToken(ConnectionSystemParameters parameters) {
    final String token = Optional.ofNullable(authnClient.getApiKey(parameters, parameters.getTenantId())
      .getHeaders()
      .get(X_OKAPI_TOKEN))
      .orElseThrow(() -> new AuthorizationException("Cannot retrieve okapi token"))
      .get(0);

    log.info("Set Okapi token " + token);
    parameters.setOkapiToken(token);
    return parameters;
  }

}
